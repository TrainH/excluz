package excluz.excluz.domain.event.service;

import excluz.excluz.common.entity.*;

import excluz.excluz.domain.event.event.enums.SelectionMethod;
import excluz.excluz.domain.event.event.repository.EventRepository;
import excluz.excluz.domain.event.event.service.EventService;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantRequestDto;
import excluz.excluz.domain.event.eventApplicant.repository.EventApplicantRepository;
import excluz.excluz.domain.event.eventApplicant.service.EventApplicantService;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EventConcurrentFirstComeTest {
    @Autowired
    private EventService eventService;

    @Autowired
    private EventApplicantService eventApplicantService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventApplicantRepository eventApplicantRepository;

    @Autowired
    private StreamerRepository streamerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final int NUMBER_OF_WINNERS = 3; // 선착순 당첨자 수
    private Event testEvent;
    private String uniqueSuffix;

    @BeforeEach
    public void setup() {
        /*
         * 테스트에 필요한 기본 데이터 생성 (Streamer, Store, Item, Event)
         * testEvent: selectionMethod가 FIRST_COME_FIRST_SERVED, numberOfWinners = 3
         * 아래는 반드시 기존 코드와 동일하게 세팅한다
         */
        // 예시: 간략화
        uniqueSuffix = UUID.randomUUID().toString().substring(0, 5);
        testEvent = createTestEvent(uniqueSuffix);
    }

    private Event createTestEvent(String suffix) {
        // 실제로는 Streamer, Store, Item 등을 생성하고,
        // EventService.createEvent(...) 로직을 통해 선착순 이벤트를 만들었다고 가정
        // ...
        // 여기서는 간단히 데이터 한 건 만든다고 가정
        Event event = new Event(
                null,  // store
                NUMBER_OF_WINNERS,
                "TEST_CODE_" + suffix,
                null,  // participantCondition
                SelectionMethod.FIRST_COME_FIRST_SERVED,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusHours(1)
        );
        return eventRepository.save(event);
    }

    @Test
    @DisplayName("비관적 락 - 동시성 테스트")
    public void testConcurrentApplicants_FirstComeFirstServed_PessimisticLock() throws Exception {
        // 1. 비관적 락 방식 사용: eventApplicantService.applyForEvent()

        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);

        // 성능 측정
        long startTime = System.currentTimeMillis();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final int idx = i;
            executorService.submit(() -> {
                try {
                    // 모든 스레드가 동시에 시작하도록 대기
                    startLatch.await();

                    String email = "pessimistic_user" + idx + "_" + uniqueSuffix + "@test.com";
                    eventApplicantService.applyForEvent(
                            testEvent.getGeneratedCode(),
                            EventApplicantRequestDto.builder()
                                    .email(email)
                                    .applicantName("사용자" + idx)
                                    .applicantPassword("password")
                                    .deliveryAddress("주소" + idx)
                                    .build()
                    );
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    System.err.println("PessimisticLock 응모 실패: " + e.getMessage());
                    exceptionCount.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await();
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // 2. 이벤트 마감 로직 (테스트용)
        eventService.closeEvent(null, testEvent.getId());

        List<EventApplicant> allApplicants = eventApplicantRepository.findByEvent(testEvent);
        long winnerCount = allApplicants.stream()
                .filter(a -> a.getApplicantStatus().name().equals("WINNER"))
                .count();

        System.out.println("===== Pessimistic Lock Test Result =====");
        System.out.println("Total applicants: " + allApplicants.size());
        System.out.println("Winner count: " + winnerCount);
        System.out.println("Success requests: " + successCount.get());
        System.out.println("Exception count: " + exceptionCount.get());
        System.out.println("Total time (ms): " + totalTime);

        assertEquals(NUMBER_OF_WINNERS, winnerCount, "당첨자는 " + NUMBER_OF_WINNERS + "명을 초과하지 않아야 함(PessimisticLock).");
    }

    @Test
    @DisplayName("낙관적 락 - 동시성 테스트")
    public void testConcurrentApplicants_FirstComeFirstServed_OptimisticLock() throws Exception {
        // 1. 낙관적 락 방식 사용: eventApplicantService.applyForEventForOptimisticLock()

        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);

        // 성능 측정
        long startTime = System.currentTimeMillis();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final int idx = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    String email = "optimistic_user" + idx + "_" + uniqueSuffix + "@test.com";
                    eventApplicantService.applyForEventForOptimisticLock(
                            testEvent.getGeneratedCode(),
                            EventApplicantRequestDto.builder()
                                    .email(email)
                                    .applicantName("사용자" + idx)
                                    .applicantPassword("password")
                                    .deliveryAddress("주소" + idx)
                                    .build()
                    );
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    System.err.println("OptimisticLock 응모 실패: " + e.getMessage());
                    exceptionCount.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await();
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // 2. 이벤트 마감 로직
        eventService.closeEvent(null, testEvent.getId());

        List<EventApplicant> allApplicants = eventApplicantRepository.findByEvent(testEvent);
        long winnerCount = allApplicants.stream()
                .filter(a -> a.getApplicantStatus().name().equals("WINNER"))
                .count();

        System.out.println("===== Optimistic Lock Test Result =====");
        System.out.println("Total applicants: " + allApplicants.size());
        System.out.println("Winner count: " + winnerCount);
        System.out.println("Success requests: " + successCount.get());
        System.out.println("Exception count: " + exceptionCount.get());
        System.out.println("Total time (ms): " + totalTime);

        assertEquals(NUMBER_OF_WINNERS, winnerCount, "당첨자는 " + NUMBER_OF_WINNERS + "명을 초과하지 않아야 함(OptimisticLock).");
    }
}
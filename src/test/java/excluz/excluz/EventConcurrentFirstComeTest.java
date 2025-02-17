package excluz.excluz;

import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.EventApplicant;
import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.Store;
import excluz.excluz.domain.event.event.dto.EventRequestDto;
import excluz.excluz.domain.event.event.dto.EventResponseDto;
import excluz.excluz.domain.event.event.enums.SelectionMethod;
import excluz.excluz.domain.event.event.repository.EventRepository;
import excluz.excluz.domain.event.event.service.EventService;
import excluz.excluz.domain.event.eventApplicant.repository.EventApplicantRepository;
import excluz.excluz.domain.event.eventApplicant.service.EventApplicantService;
import excluz.excluz.domain.event.eventItem.dto.EventItemRequestDto;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
    private StoreRepository storeRepository;

    @Autowired
    private ItemRepository itemRepository;

    // 테스트 전 기본 데이터 세팅 (Store, Item, Event 생성)
    private Store store;
    private Item testItem;
    private Event event;
    private final int NUMBER_OF_WINNERS = 3; // 선착순 당첨자 수
    private String uniqueSuffix;

    @BeforeEach
    public void setup() {
        uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);

        store = Store.builder()
                .streamer(null)  // 단순 테스트를 위해 null 처리하거나 미리 등록된 Streamer 사용
                .address("테스트 주소" + uniqueSuffix)
                .storeName("테스트 스토어" + uniqueSuffix)
                .registrationNumber("123450" + uniqueSuffix)
                .build();
        storeRepository.save(store);

        // Item 생성 (스토어에 소속, 잔여수량 충분한 상태)
        testItem = Item.builder()
                .store(store)
                .itemName("동시성 테스트 아이템" + uniqueSuffix)
                .explanation("설명")
                .price(1000)
                .remainingQuantity(100)
                .build();
        itemRepository.save(testItem);

        // 이벤트 생성 (FIRST_COME_FIRST_SERVED 방식)
        EventRequestDto eventRequestDto = EventRequestDto.builder()
                .storeId(store.getId())
                .numberOfWinners(NUMBER_OF_WINNERS)
                .participantCondition("ALL_USERS") // enum 값 문자열 (예)
                .selectionMethod(SelectionMethod.FIRST_COME_FIRST_SERVED.name())
                .startDatetime(LocalDateTime.now().minusMinutes(1))  // 이미 시작된 이벤트
                .endDatetime(LocalDateTime.now().plusHours(1))
                .eventItemList(Arrays.asList(
                        EventItemRequestDto.builder()
                                .itemId(testItem.getId())
                                .quantity(1)
                                .build()
                ))
                .build();

        EventResponseDto responseDto = eventService.createEvent(eventRequestDto);
        event = eventRepository.findById(responseDto.getId())
                .orElseThrow(() -> new IllegalStateException("생성된 이벤트가 없습니다."));
    }

    @Test
    public void testConcurrentApplicants_FirstComeFirstServed() throws Exception {
        // 동시성 테스트를 위한 스레드 풀과 CountDownLatch 준비
        int numberOfThreads = 100; // 동시에 n명의 응모 시도
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1); // 모든 스레드를 동시에 시작
        CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);

        // 각 스레드에서 고유의 이메일과 이름으로 응모 요청 수행
        for (int i = 0; i < numberOfThreads; i++) {
            final int idx = i;
            String uniqueApplicantCode = uniqueSuffix + idx;
            executorService.submit(() -> {
                try {
                    // 모든 스레드가 동시에 시작하도록 대기
                    startLatch.await();

                    // 각 신청자의 정보: 이메일, 비밀번호 등은 다르게 생성
                    String email = "user" + uniqueApplicantCode + "@test.com";
                    // 응모 요청 (EventApplicantService.applyForEvent)
                    eventApplicantService.applyForEvent(
                            event.getGeneratedCode(),
                            // 간단한 DTO 생성 (빌더 사용)
                            excluz.excluz.domain.event.eventApplicant.dto.EventApplicantRequestDto.builder()
                                    .email(email)
                                    .applicantName("사용자" + uniqueApplicantCode)
                                    .applicantPassword("password" + uniqueApplicantCode)
                                    .deliveryAddress("주소" + uniqueApplicantCode)
                                    .build()
                    );
                } catch (Exception e) {
                    // 실제 응모 실패 원인 로깅 필요
                    System.out.println("응모 실패: " + e.getMessage());
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // 모든 스레드 시작
        startLatch.countDown();
        // 모든 스레드 완료될 때까지 기다림
        finishLatch.await();
        executorService.shutdown();

        // 모든 응모 처리 후, 이벤트 마감 로직 실행 (선착순 기준으로 당첨자 판별)
        // 동시성 상황에서 저장된 응모는 createdAt 값 순으로 정렬됨.
        var closingResponse = eventService.closeEvent(event.getId());

        // 당첨자 수가 NUMBER_OF_WINNERS로 제한되었는지 확인
        List<EventApplicant> allApplicants = eventApplicantRepository.findByEvent(event);
        long winnerCount = allApplicants.stream()
                .filter(applicant -> applicant.getApplicantStatus().name().equals("WINNER"))
                .count();

        System.out.println("전체 응모자 수: " + allApplicants.size());
        System.out.println("당첨자 수: " + winnerCount);

        assertEquals(NUMBER_OF_WINNERS, winnerCount,
                "동시 응모 상황에서도 당첨자는 선착순 기준으로 NUMBER_OF_WINNERS명으로 제한되어야 합니다.");

        // 추가 검증: 신청 시각 기준 오름차순 정렬 후, 상위 NUMBER_OF_WINNERS가 당첨되었는지 확인 가능
        allApplicants.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        for (int i = 0; i < allApplicants.size(); i++) {
            if (i < NUMBER_OF_WINNERS) {
                assertEquals("WINNER", allApplicants.get(i).getApplicantStatus().name(),
                        "첫 " + NUMBER_OF_WINNERS + "명은 당첨되어야 합니다.");
            } else {
                assertNotEquals("WINNER", allApplicants.get(i).getApplicantStatus().name(),
                        "당첨자 수를 초과한 나머지는 당첨 상태가 아니어야 합니다.");
            }
        }
    }


}

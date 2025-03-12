package excluz.excluz.domain.event.service;

import excluz.excluz.common.entity.*;

import excluz.excluz.domain.event.event.enums.ParticipantCondition;
import excluz.excluz.domain.event.event.enums.SelectionMethod;
import excluz.excluz.domain.event.event.repository.EventRepository;
import excluz.excluz.domain.event.event.service.EventService;
import excluz.excluz.domain.event.eventApplicant.dto.request.EventApplicantRequestDto;
import excluz.excluz.domain.event.eventApplicant.repository.EventApplicantRepository;
import excluz.excluz.domain.event.eventApplicant.service.EventApplicantService;
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

    private final int NUMBER_OF_WINNERS = 3; // 선착순 당첨자 수
    private final int NUMBER_OF_THREADS = 100;
    private Streamer testStreamer;
    private Streamer savedStreamer;
    private Store testStore;
    private Event testEvent;
    private String uniqueSuffix;

    @BeforeEach
    public void setup() {
        /*
         * 테스트에 필요한 기본 데이터 생성 (Streamer, Store, Item, Event)
         * testEvent: selectionMethod가 FIRST_COME_FIRST_SERVED, numberOfWinners = 3
         * 아래는 반드시 기존 코드와 동일하게 세팅한다
         */

        uniqueSuffix = UUID.randomUUID().toString().substring(0, 5);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int middle = random.nextInt(1000, 10000); // 1000 이상 10000 미만 → 4자리
        int last = random.nextInt(1000, 10000);   // 4자리

        testStreamer = Streamer.builder()
                .name("스트리머" + uniqueSuffix)
                .nickName("스트리머" + uniqueSuffix)
                .phoneNumber("010-" + middle + "-" + last)
                .email("streamer" + uniqueSuffix + "@example.com")
                .password("password")
                .build();
        streamerRepository.save(testStreamer);
        savedStreamer = streamerRepository.findByEmail(testStreamer.getEmail())
                .orElseThrow();

        testStore = Store.builder()
                .streamer(testStreamer)
                .storeName("StoreName" + uniqueSuffix)
                .address("StoreAddress" + uniqueSuffix)
                .registrationNumber("RegNum" + uniqueSuffix)
                .build();

        storeRepository.save(testStore);

        testEvent = new Event(
                testStore,  // store
                NUMBER_OF_WINNERS,
                "TEST_CODE_" + uniqueSuffix,
                ParticipantCondition.ALL_USERS,  // participantCondition
                SelectionMethod.FIRST_COME_FIRST_SERVED,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusHours(1)
        );

        testEvent = eventRepository.save(testEvent);

    }

    @Test
    @DisplayName("비관적 락 - 동시성 테스트")
    public void testConcurrentApplicants_FirstComeFirstServed_PessimisticLock() throws Exception {
        // 1. 비관적 락 방식 사용: eventApplicantService.applyForEvent()

        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(NUMBER_OF_THREADS);

        // 성능 측정
        long startTime = System.currentTimeMillis();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int idx = i;
            executorService.submit(() -> {
                try {
                    // 모든 스레드가 동시에 시작하도록 대기
                    startLatch.await();

                    String email = "pessimistic_user" + idx + "_" + uniqueSuffix + "@test.com";
                    System.out.println("🚀 [CALL] 스레드 " + idx + " 이벤트 신청 서비스 호출 직전 (email=" + email + ")");
                    eventApplicantService.applyForEvent(
                            testEvent.getGeneratedCode(),
                            EventApplicantRequestDto.builder()
                                    .email(email)
                                    .applicantName("사용자" + idx)
                                    .applicantPassword("password")
                                    .deliveryAddress("주소" + idx)
                                    .build()
                    );
                    System.out.println("✅ [DONE] " + idx + "이벤트 응모 완료");
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
        eventService.closeEvent(savedStreamer.getId(), testEvent.getId());

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
        System.out.println("🏁 [START] Optimistic Lock 테스트 시작");
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(NUMBER_OF_THREADS);

        // 성능 측정
        long startTime = System.currentTimeMillis();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int idx = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    String email = "optimistic_user" + idx + "_" + uniqueSuffix + "@test.com";
                    System.out.println("🚀 [CALL] 스레드 " + idx + " 이벤트 신청 서비스 호출 직전 (email=" + email + ")");
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
                    System.err.println("💥 [EXCEPTION] thread " + idx + " - " + e.getClass().getSimpleName() + " : " + e.getMessage());
//                    System.err.println("OptimisticLock 응모 실패: " + e.getMessage());
                    exceptionCount.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await();
        System.out.println("🎉 [FINISH] 모든 스레드 완료 대기 끝");
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // 2. 이벤트 마감 로직
        eventService.closeEvent(savedStreamer.getId(), testEvent.getId());

        List<EventApplicant> allApplicants = eventApplicantRepository.findByEvent(testEvent);
        long winnerCount = allApplicants.stream()
                .filter(a -> a.getApplicantStatus().name().equals("WINNER"))
                .count();

        System.out.println("📊 [RESULT] 신청자 수, 당첨자 수 집계 시작");
        System.out.println("===== Optimistic Lock Test Result =====");
        System.out.println("Total applicants: " + allApplicants.size());
        System.out.println("Winner count: " + winnerCount);
        System.out.println("Success requests: " + successCount.get());
        System.out.println("Exception count: " + exceptionCount.get());
        System.out.println("Total time (ms): " + totalTime);

        assertEquals(NUMBER_OF_WINNERS, winnerCount, "당첨자는 " + NUMBER_OF_WINNERS + "명을 초과하지 않아야 함(OptimisticLock).");
    }

    //   이하 학습을 위한 예시들
    /*
    • "CountDownLatch"는 JDK의 java.util.concurrent 패키지에 속하는 클래스
    • 기본적으로 어떤 지점(또는 지연)을 "대기(latch)”시킬 때, 래치(latch)에 설정해 둔 숫자가 0이 될 때까지 계속 대기함.
    • 여러 스레드가 동시에 시작해야 하거나 여러 스레드의 작업이 모두 끝나야 다음 단계를 진행할 수 있을 때 자주 사용됨.
     */

    @Test
    void basicCountDownLatchTest() throws InterruptedException {
         /*
          [학습 개념]
          - CountDownLatch(1)을 생성하면, countDown()을 한 번 호출했을 때 latch가 0이 되어서 대기가 풀린다.
          - await()을 통해 해당 latch가 0이 되기까지 기다릴 수 있다.
         */

        System.out.println("🏁 [START] basicCountDownLatchTest");

        // CountDownLatch(1) -> latch의 초기 값 1
        CountDownLatch latch = new CountDownLatch(1);

        Thread threadA = new Thread(() -> {
            System.out.println("🚀 [CALL] Thread A started, about to await latch...");
            try {
                // latch가 0이 될 때까지 대기
                latch.await();
                System.out.println("✅ [RESUME] Thread A resumed after latch.countDown()");
            } catch (InterruptedException e) {
                System.err.println("💥 [EXCEPTION] Thread A - " + e.getClass().getSimpleName() + " : " + e.getMessage());
            }
        });

        threadA.start();

        // 메인 스레드에서 약간의 시간 후 latch.countDown()
        Thread.sleep(1000);
        System.out.println("🔔 [NOTIFY] Main thread calls latch.countDown()");
        latch.countDown();

        // Thread A가 종료되길 기다림
        threadA.join();
        System.out.println("🏁 [END] basicCountDownLatchTest");
    }

    @Test
    void concurrentStartTest() throws InterruptedException {
         /*
          [학습 개념]
          - 여러 스레드가 동시에 작업을 시작하도록 만들기 위해, startLatch(1)를 두고
            각 스레드에서 startLatch.await()를 호출한다.
          - 주 스레드에서 startLatch.countDown()을 호출함으로써 모든 스레드가 동시에 실행을 시작한다.
          - finishLatch를 통해 모든 스레드가 작업을 마칠 때까지 대기할 수도 있다.
         */

        System.out.println("🏁 [START] concurrentStartTest");

        int numberOfThreads = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            final int idx = i;
            Thread thread = new Thread(() -> {
                try {
                    System.out.println("🚀 [CALL] Thread " + idx + " waiting for startLatch...");
                    startLatch.await();  // startLatch가 0이 될 때까지 대기
                    System.out.println("✅ [RESUME] Thread " + idx + " - started!");
                    // (여기에 실제 로직이 들어간다고 가정)
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    System.err.println("💥 [EXCEPTION] Thread " + idx + " - " + e.getClass().getSimpleName() + " : " + e.getMessage());
                } finally {
                    finishLatch.countDown(); // 작업 마침
                }
            });
            thread.start();
        }

        // 모든 스레드 준비 후, 동시에 시작 시키기
        Thread.sleep(500);
        System.out.println("🔔 [NOTIFY] Main thread calls startLatch.countDown()");
        startLatch.countDown();

        // 모든 스레드가 끝날 때까지 대기
        finishLatch.await();
        System.out.println("🏁 [END] concurrentStartTest");

    }

    @Test
    void concurrencyServiceTest() throws InterruptedException {
         /*
          [학습 개념]
          - 동시에 시작하는 패턴(startLatch) + 모두 끝나는 패턴(finishLatch)을 이용해
            여러 스레드가 실제 동시성 로직(예: DB insert, Lock)을 테스트한다.
          - AtomicInteger로 성공/실패 횟수를 카운트해볼 수 있다.
         */

        System.out.println("🏁 [START] concurrencyServiceTest");

        int numberOfThreads = 10;

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final int idx = i;
            new Thread(() -> {
                try {
                    System.out.println("🚀 [CALL] Thread " + idx + " waiting for startLatch...");
                    startLatch.await(); // 동시에 시작
                    // 실제 동시성 로직 테스트 (예: eventApplicantService.applyForEvent)
                    // 여기서는 단순히 짝수/홀수로 성공/실패를 시뮬레이션
                    if (idx % 2 == 0) {
                        successCount.incrementAndGet();
                        System.out.println("✅ [SUCCESS] Thread " + idx + " processed normally");
                    } else {
                        throw new RuntimeException("테스트용 예외");
                    }
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                    System.err.println("💥 [EXCEPTION] Thread " + idx + " - " + e.getClass().getSimpleName() + " : " + e.getMessage());
                } finally {
                    finishLatch.countDown();
                }
            }).start();
        }

        Thread.sleep(500);
        System.out.println("🔔 [NOTIFY] Main thread calls startLatch.countDown()");
        startLatch.countDown();

        finishLatch.await();
        System.out.println("🏁 [END] concurrencyServiceTest");

        // 결과 출력
        System.out.println("📊 successCount = " + successCount.get());
        System.out.println("📊 exceptionCount = " + exceptionCount.get());
    }

    //    ExcecutorService 학습 예제
    @Test
    void executorServiceSingleTask() throws InterruptedException {
         /*
          [학습 개념]
          - ExecutorService 객체 생성: Executors.newSingleThreadExecutor() 또는 newFixedThreadPool() 등 다양.
          - 단 하나의 Runnable(또는 Callable)을 submit해서 비동기로 실행.
          - 작업 완료 후, shutdown() 호출로 스레드 풀을 정리.
          */

        System.out.println("🏁 [START] executorServiceSingleTask");

        // 1) ExecutorService 생성 (단일 스레드 풀 예시)
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // 2) Runnable(또는 Callable) 제출
        executorService.submit(() -> {
            System.out.println("🚀 [CALL] Task 1 started (single thread)");
            try {
                Thread.sleep(500);
                System.out.println("✅ [DONE] Task 1 complete!");
            } catch (InterruptedException e) {
                System.err.println("💥 [EXCEPTION] " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        });

        // 3) ExecutorService 종료
        executorService.shutdown();
        // (선택) 종료를 기다리기
        if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        System.out.println("🏁 [END] executorServiceSingleTask");
    }


    @Test
    void executorServiceMultipleTasks() throws InterruptedException {

    /*
     [학습 개념]
     - newFixedThreadPool()로 고정된 수의 스레드를 갖는 풀 생성.
     - 여러 Runnable을 submit -> 풀 내 스레드가 작업을 분산 처리.
     - 스레드 풀 종료 시점( awaitTermination ) 확인.
     */

        System.out.println("🏁 [START] executorServiceMultipleTasks");

        // 1) 고정 스레드 풀 (스레드 3개)
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // 2) 여러 작업 제출
        for (int i = 0; i < 5; i++) {
            final int idx = i;
            executorService.submit(() -> {
                System.out.println("🚀 [CALL] Task " + idx + " started");
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                    System.out.println("✅ [DONE] Task " + idx + " complete!");
                } catch (InterruptedException e) {
                    System.err.println("💥 [EXCEPTION] Task " + idx + " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            });
        }

        // 3) 스레드 풀 종료
        executorService.shutdown();
        if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        System.out.println("🏁 [END] executorServiceMultipleTasks");
    }

    @Test
    void executorServiceWithCallable() throws InterruptedException, ExecutionException {

    /*
     [학습 개념]
     - Runnable은 반환값이 없지만 Callable은 반환값을 가질 수 있다.
     - ExecutorService.submit(Callable)을 통해 Future를 얻을 수 있다.
     - Future.get()을 호출하면 작업 결과를 받거나 예외가 발생한다.
     */

        System.out.println("🏁 [START] executorServiceWithCallable");

        // 1) ExecutorService 생성 (고정 스레드 2개)
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 2) Callable 작업 제출 -> Future 반환
        Callable<String> callableTask = () -> {
            System.out.println("🚀 [CALL] Callable task started");
            Thread.sleep(500);
            // 반환할 결과
            System.out.println("⛏ [WORK] Work has done before returning result...");
            return "Callable Result OK";
        };

        Future<String> futureResult = executorService.submit(callableTask);

        // 3) 다른 작업도 제출 가능
        executorService.submit(() -> System.out.println("🚀✅ [CALL] Another Runnable running in parallel..."));

        // 4) Future.get() -> 결과 받기 (블록 메서드)
        String result = futureResult.get();
        System.out.println("✅ [DONE] Received result from Callable: " + result);

        // 5) ExecutorService 종료
        executorService.shutdown();
        if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        System.out.println("🏁 [END] executorServiceWithCallable");
    }

    @Test
    void concurrency_withCountDownLatch_and_ExecutorService() throws InterruptedException {
         /*
          [학습 개념]
          - 스레드를 직접 생성 대신, ExecutorService를 통해 고정 개수(NUMBER_OF_THREADS)의 풀을 만들어 사용.
          - startLatch(1)를 통해 동시에 시작, finishLatch(NUMBER_OF_THREADS)를 통해 모두 종료될 때까지 대기.
          - 실제론 DB 트랜잭션, 락, shared resource 등에 접근하는 로직을 수행하게 됨.
         */

        System.out.println("🏁 [START] concurrency_withCountDownLatch_and_ExecutorService");

        int NUMBER_OF_THREADS = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(NUMBER_OF_THREADS);

        long startTime = System.currentTimeMillis();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int idx = i;
            executorService.submit(() -> {
                try {
                    System.out.println("🚀 [CALL] Thread " + idx + " waiting for startLatch...");
                    startLatch.await(); // 동시에 시작

                    // 동시성 테스트 로직 (예: 비관적 락, 낙관적 락 등)
                    // 여기서는 짝수면 성공, 홀수면 예외라고 가정
                    if (idx % 2 == 0) {
                        successCount.incrementAndGet();
                        System.out.println("✅ [SUCCESS] Thread " + idx + " processed normally");
                    } else {
                        throw new RuntimeException("테스트용 예외");
                    }
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                    System.err.println("💥 [EXCEPTION] Thread " + idx + " - " + e.getClass().getSimpleName() + " : " + e.getMessage());
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // 메인 스레드에서 카운트를 내려주면 모든 스레드가 동시에 시작
        Thread.sleep(500);
        System.out.println("🔔 [NOTIFY] Main thread calls startLatch.countDown()");
        startLatch.countDown();

        // 모든 스레드가 끝날 때까지 대기
        finishLatch.await();
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("🏁 [END] concurrency_withCountDownLatch_and_ExecutorService");
        System.out.println("📊 Success count: " + successCount.get());
        System.out.println("📊 Exception count: " + exceptionCount.get());
        System.out.println("⏱️ Total time (ms): " + totalTime);

        // 적절한 검증 로직(예: 결과값 검증)
        // assertEquals(5, successCount.get());

    }



}
package excluz.excluz.domain.event.service;


import excluz.excluz.common.entity.*;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.UnauthorizedException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.event.event.dto.EventClosingResponseDto;
import excluz.excluz.domain.event.event.dto.EventRequestDto;
import excluz.excluz.domain.event.event.dto.EventResponseDto;
import excluz.excluz.domain.event.event.enums.ParticipantCondition;
import excluz.excluz.domain.event.event.enums.SelectionMethod;
import excluz.excluz.domain.event.event.repository.EventRepository;
import excluz.excluz.domain.event.event.service.EventService;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantRequestDto;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantResponseDto;
import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import excluz.excluz.domain.event.eventApplicant.repository.EventApplicantRepository;
import excluz.excluz.domain.event.eventApplicant.service.EventApplicantService;
import excluz.excluz.domain.event.eventItem.dto.EventItemRequestDto;
import excluz.excluz.domain.event.eventItem.repository.EventItemRepository;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EventServiceTest {
    // 모의 객체 (각 Repository)
    @Mock
    private StreamerRepository streamerRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventItemRepository eventItemRepository;
    @Mock
    private EventApplicantRepository eventApplicantRepository;

    // 실제 테스트 객체
    @InjectMocks
    private EventService eventService;
    @InjectMocks
    private EventApplicantService eventApplicantService;

    // 테스트에서 사용할 엔티티들
    private Streamer testStreamer;
    private Store testStore;
    private Item testItem1;
    private Item testItem2;
    private EventApplicantRequestDto testRequestDto;
    private String uniqueSuffix;
    private final String testCode = "TEST_CODE";
    private Event testEvent;

    @BeforeEach
    public void setup() {
        // 각 테스트마다 중복 데이터를 피하기 위한 고유 접미사
        uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);

        // 단순 생성 (필요시 빌더 사용)
        testStreamer = Streamer.builder()
                .name("스트리머" + uniqueSuffix)
                .nickName("스트리머" + uniqueSuffix)
                .phoneNumber("01012341234")
                .email("streamer" + uniqueSuffix + "@example.com")
                .password("password")
                .build();
        ReflectionTestUtils.setField(testStreamer, "id", 1);

        testStore = Store.builder()
                .streamer(testStreamer)
                .storeName("StoreName" + uniqueSuffix)
                .address("StoreAddress" + uniqueSuffix)
                .registrationNumber("RegNum" + uniqueSuffix)
                .build();
        ReflectionTestUtils.setField(testStore, "id", 1);

        testItem1 = Item.builder()
                .store(testStore)
                .itemName("TestItem" + uniqueSuffix + "1")
                .explanation("설명")
                .price(5000)
                .remainingQuantity(100)
                .build();
        ReflectionTestUtils.setField(testItem1, "id", 1);

        testItem2 = Item.builder()
                .store(testStore)
                .itemName("TestItem" + uniqueSuffix + "2")
                .explanation("설명2")
                .price(10000)
                .remainingQuantity(100)
                .build();
        ReflectionTestUtils.setField(testItem2, "id", 2);



        when(streamerRepository.findById(testStreamer.getId()))
                .thenReturn(Optional.of(testStreamer));

        when(storeRepository.findById(testStore.getId()))
                .thenReturn(Optional.of(testStore));

        when(itemRepository.findById(testItem1.getId()))
                .thenReturn(Optional.of(testItem1));
        when(itemRepository.findById(testItem2.getId()))
                .thenReturn(Optional.of(testItem2));

        when(storeRepository.save(any(Store.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(streamerRepository.save(any(Streamer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
//         when(eventRepository.save(any(Event.class)))
//                 .thenAnswer(invocation -> {
//                     Event e = invocation.getArgument(0);
//                     ReflectionTestUtils.setField(e, "id", 1);
//                     return e;
//                 });


        testRequestDto = EventApplicantRequestDto.builder()
                .email("test@example.com")
                .applicantName("테스트사용자")
                .applicantPassword("password")
                .deliveryAddress("주소")
                .build();

        testEvent = createTestEvent();
    }

// --------------------1. createEvent 테스트--------------------

    @Test
    @DisplayName("success: 이벤트 생성")
    public void testCreateEventSuccess() {
        // given
        List<EventItemRequestDto> eventItemList = new ArrayList<>();
        eventItemList.add(new EventItemRequestDto(testItem1.getId(), 5));
        eventItemList.add(new EventItemRequestDto(testItem2.getId(), 2));
        EventRequestDto requestDto = EventRequestDto.builder()
                .storeId(testStore.getId())
                .numberOfWinners(3)
                .participantCondition(ParticipantCondition.ALL_USERS.name())
                .selectionMethod(SelectionMethod.RANDOM_DRAW.name())
                .startDatetime(LocalDateTime.now().minusMinutes(1))
                .endDatetime(LocalDateTime.now().plusDays(1))
                .eventItemList(eventItemList)
                .build();

        // when
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            ReflectionTestUtils.setField(event, "id", 1);
            return event;
        });

        EventResponseDto responseDto = eventService.createEvent(testStreamer.getId(), requestDto);

        // then
        Assertions.assertThat(responseDto).isNotNull();
        Assertions.assertThat(responseDto.getId()).isNotNull();
        Assertions.assertThat(responseDto.getStreamerStoreId()).isEqualTo(testStore.getId());
        Assertions.assertThat(responseDto.getNumberOfWinners()).isEqualTo(3);
        Assertions.assertThat(responseDto.getIsCompleted()).isFalse();
        Assertions.assertThat(responseDto.getParticipantCondition()).isEqualTo(ParticipantCondition.ALL_USERS.name());
        Assertions.assertThat(responseDto.getSelectionMethod()).isEqualTo(SelectionMethod.RANDOM_DRAW.name());
        Assertions.assertThat(responseDto.getGeneratedCode()).isNotNull();
        Assertions.assertThat(responseDto.getEventItemList()).hasSize(2);

        // 추가: 이벤트 저장 호출 여부 검증
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("fail: 잘못된 스토어 ID로 이벤트 생성 시")
    public void testCreateEventFailure_InvalidStore() {
        // given
        List<EventItemRequestDto> eventItemList = new ArrayList<>();
        eventItemList.add(new EventItemRequestDto(testItem1.getId(), 5));
        EventRequestDto requestDto = EventRequestDto.builder()
                .storeId(-999)   // 존재하지 않는 스토어 ID
                .numberOfWinners(3)
                .participantCondition(ParticipantCondition.ALL_USERS.name())
                .selectionMethod(SelectionMethod.RANDOM_DRAW.name())
                .startDatetime(LocalDateTime.now().minusMinutes(1))
                .endDatetime(LocalDateTime.now().plusDays(1))
                .eventItemList(eventItemList)
                .build();

        when(storeRepository.findById(-999)).thenReturn(Optional.empty());

        // when, then
        Assertions.assertThatThrownBy(() -> {
                    eventService.createEvent(testStreamer.getId(), requestDto);
                })
                .isInstanceOf(NotFoundException.class)
                .satisfies(ex -> {
                    NotFoundException notFoundEx = (NotFoundException) ex;
                    Assertions.assertThat(notFoundEx.getErrorCode()).isEqualTo(ErrorCode.STORE_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("fail: 다른 사람의 스토어로 이벤트 생성 시 -> STORE_NOT_MATCH")
    public void testCreateEventFailure_StoreNotMatch() {
        // given
        // testStreamer(id=1)와는 다른 스트리머 생성
        String tempSuffix = UUID.randomUUID().toString().substring(0, 4);
        Streamer streamer2 = Streamer.builder()
                .name("스트리머" + tempSuffix)
                .nickName("스트리머" + tempSuffix)
                .phoneNumber("01000000000")
                .email("streamer2" + tempSuffix + "@example.com")
                .password("password")
                .build();
        ReflectionTestUtils.setField(streamer2, "id", 2);
    }

    @Test
    @DisplayName("fail: 스토어에 소속되지 않은 아이템으로 이벤트 생성 시")
    public void testCreateEventFailure_ItemNotBelongToStore() {
        // given
        // 타 스토어 소속 아이템 준비
        String tempSuffix = UUID.randomUUID().toString().substring(0, 4);
        Streamer streamer2 = Streamer.builder()
                .name("스트리머" + tempSuffix)
                .nickName("스트리머" + tempSuffix)
                .phoneNumber("01000000000")
                .email("streamer2" + tempSuffix + "@example.com")
                .password("password")
                .build();
        ReflectionTestUtils.setField(streamer2, "id", 2);
        Store otherStore = Store.builder()
                .streamer(streamer2)
                .storeName("OtherStore" + tempSuffix)
                .address("OtherAddress" + tempSuffix)
                .registrationNumber("OtherReg" + tempSuffix)
                .build();
        ReflectionTestUtils.setField(otherStore, "id", 2);
        Item otherItem = Item.builder()
                .store(otherStore)
                .itemName("OtherItem" + tempSuffix)
                .explanation("Other Desc")
                .price(7000)
                .remainingQuantity(50)
                .build();
        ReflectionTestUtils.setField(otherItem, "id", 3);

        List<EventItemRequestDto> eventItemList = new ArrayList<>();
        // 기본 스토어(testStore)와 다른 아이템 사용
        eventItemList.add(new EventItemRequestDto(otherItem.getId(), 2));
        EventRequestDto requestDto = EventRequestDto.builder()
                .storeId(testStore.getId())  // 기본 스토어 id 사용
                .numberOfWinners(3)
                .participantCondition(ParticipantCondition.ALL_USERS.name())
                .selectionMethod(SelectionMethod.RANDOM_DRAW.name())
                .startDatetime(LocalDateTime.now().minusMinutes(1))
                .endDatetime(LocalDateTime.now().plusDays(1))
                .eventItemList(eventItemList)
                .build();

        when(storeRepository.findById(testStore.getId())).thenReturn(Optional.of(testStore));
        // 해당 아이템 id 조회 시, 타 스토어 소속 아이템 반환
        when(itemRepository.findById(otherItem.getId())).thenReturn(Optional.of(otherItem));

        // when, then
        Assertions.assertThatThrownBy(() -> {
                    eventService.createEvent(testStreamer.getId(), requestDto);
                }).isInstanceOf(UnauthorizedException.class)
                .satisfies(ex -> {
                    UnauthorizedException notFoundEx = (UnauthorizedException) ex;
                    Assertions.assertThat(notFoundEx.getErrorCode()).isEqualTo(ErrorCode.ITEM_NOT_MATCH);
                });
    }

    @Test
    @DisplayName("fail: 이미 과거인 종료 시간으로 이벤트 생성 시 -> EVENT_ENDDATETIME_TOO_EARLY")
    public void testCreateEventFailure_EndDateTimeTooEarly() {
        // given
        List eventItemList = Collections.singletonList(
                new EventItemRequestDto(testItem1.getId(), 5)
        );
        // 종료 시간이 현재시간보다 과거가 되도록 설정
        EventRequestDto requestDto = EventRequestDto.builder()
                .storeId(testStore.getId())
                .numberOfWinners(3)
                .participantCondition(ParticipantCondition.ALL_USERS.name())
                .selectionMethod(SelectionMethod.RANDOM_DRAW.name())
                .startDatetime(LocalDateTime.now().minusMinutes(10))
                .endDatetime(LocalDateTime.now().minusMinutes(1)) // 과거 시간
                .eventItemList(eventItemList)
                .build();
        // when, then
        Assertions.assertThatThrownBy(() -> eventService.createEvent(testStreamer.getId(), requestDto))
                .isInstanceOf(BadRequestException.class)
                .satisfies(ex -> {
                    BadRequestException be = (BadRequestException) ex;
                    Assertions.assertThat(be.getErrorCode()).isEqualTo(ErrorCode.EVENT_ENDDATETIME_TOO_EARLY);
                });
    }

    @Test
    @DisplayName("fail: 존재하지 않는 아이템으로 이벤트 생성 시 -> ITEM_NOT_FOUND")
    public void testCreateEventFailure_ItemNotFound() {
        // given
        int nonExistentItemId = 999;
        when(itemRepository.findById(nonExistentItemId)).thenReturn(Optional.empty());
        List<EventItemRequestDto> eventItemList = Collections.singletonList(
                new EventItemRequestDto(nonExistentItemId, 5)
        );
        EventRequestDto requestDto = EventRequestDto.builder()
                .storeId(testStore.getId())
                .numberOfWinners(3)
                .participantCondition(ParticipantCondition.ALL_USERS.name())
                .selectionMethod(SelectionMethod.RANDOM_DRAW.name())
                .startDatetime(LocalDateTime.now().minusMinutes(1))
                .endDatetime(LocalDateTime.now().plusDays(1))
                .eventItemList(eventItemList)
                .build();

        // when, then
        Assertions.assertThatThrownBy(() -> eventService.createEvent(testStreamer.getId(), requestDto))
                .isInstanceOf(NotFoundException.class)
                .satisfies(ex -> {
                    NotFoundException nfe = (NotFoundException) ex;
                    Assertions.assertThat(nfe.getErrorCode()).isEqualTo(ErrorCode.ITEM_NOT_FOUND);
                });
    }


// --------------------2. getAllEvents, getEvent 테스트--------------------

    @Test
    @DisplayName("success: 전체 이벤트 목록 조회")
    public void testGetAllEventsSuccess() {
        // given
        Event event1 = createTestEvent();
        ReflectionTestUtils.setField(event1, "id", 1);
        Event event2 = createTestEvent();
        ReflectionTestUtils.setField(event2, "id", 2);
        List<Event> events = Arrays.asList(event1, event2);
        when(eventRepository.findAll()).thenReturn(events);

        // when
        List<EventResponseDto> eventList = eventService.getAllEvents();

        // then
        Assertions.assertThat(eventList).isNotNull();
        Assertions.assertThat(eventList.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("success: 단건 이벤트 조회")
    public void testGetEventSuccess() {
        // given
        Event event = createTestEvent();
        ReflectionTestUtils.setField(event, "id", 1);
        // stubbing: 이벤트 단건 조회
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        // stubbing: 이벤트 아이템 조회 (간단히 빈 리스트)
        when(eventItemRepository.findByEvent(event)).thenReturn(new ArrayList<>());

        // when
        EventResponseDto fetchedEvent = eventService.getEvent(1);

        // then
        Assertions.assertThat(fetchedEvent).isNotNull();
        Assertions.assertThat(fetchedEvent.getId()).isEqualTo(1);
        Assertions.assertThat(fetchedEvent.getEventItemList()).isNotNull();
    }

    @Test
    @DisplayName("fail: 존재하지 않는 이벤트 단건 조회")
    public void testGetEventFailure_NotFound() {
        // given
        when(eventRepository.findById(-999)).thenReturn(Optional.empty());

        // when, then
        Assertions.assertThatThrownBy(() -> {
                    eventService.getEvent(-999);
                })
                .isInstanceOf(NotFoundException.class)
                .satisfies(ex -> {
                    NotFoundException nfe = (NotFoundException) ex;
                    Assertions.assertThat(nfe.getErrorCode()).isEqualTo(ErrorCode.EVENT_NOT_FOUND);
                });
    }

// --------------------3. closeEvent 테스트--------------------

    @Test
    @DisplayName("success: 이벤트 마감")
    public void testCloseEventSuccess() {
        // given
        Event event = createTestEvent();
        ReflectionTestUtils.setField(event, "id", 1);
        ReflectionTestUtils.setField(event, "numberOfWinners", 3);
        ReflectionTestUtils.setField(event, "isCompleted", false);
        ReflectionTestUtils.setField(event, "selectionMethod", SelectionMethod.RANDOM_DRAW);

        // stubbing: 조회
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        // stubbing: 이벤트 마감 후, eventApplicantRepository.findByEvent()에서 10명의 지원자 반환
        List<EventApplicant> applicantList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            EventApplicant applicant = EventApplicant.builder()
                    .event(event)
                    .email("applicant" + i + "@example.com")
                    .applicantName("지원자" + i)
                    .applicantPassword("password")
                    .deliveryAddress("Address" + i)
                    .applicantStatus(ApplicantStatus.WAITING)
                    .build();
            ReflectionTestUtils.setField(applicant, "id", i + 1);
            applicantList.add(applicant);
        }
        when(eventApplicantRepository.findByEvent(event)).thenReturn(applicantList);

        // when
        EventClosingResponseDto closingResponse = eventService.closeEvent(testStreamer.getId(), 1);

        // then
        Assertions.assertThat(closingResponse).isNotNull();
        Assertions.assertThat(closingResponse.getId()).isEqualTo(1);
        Assertions.assertThat(closingResponse.getIsCompleted()).isTrue();
        Assertions.assertThat(closingResponse.getEventApplicants()).hasSize(10);

        // 당첨자 수 검증 (예시로 실제 로직이 당첨자 수 업데이트 후 ApplicantStatus 변경)
        int winnerCount = (int) closingResponse.getEventApplicants().stream().filter(a -> a.getApplicantStatus() == ApplicantStatus.WINNER).count();
        Assertions.assertThat(winnerCount).isEqualTo(event.getNumberOfWinners());
    }


    @Test
    @DisplayName("fail: 존재하지 않는 이벤트로 closeEvent 호출 -> EVENT_NOT_FOUND")
    public void testCloseEventFailure_EventNotFound() {
        // given
        when(eventRepository.findById(-999)).thenReturn(Optional.empty());

        // when, then
        Assertions.assertThatThrownBy(() -> eventService.closeEvent(testStreamer.getId(), -999))
                .isInstanceOf(NotFoundException.class)
                .satisfies(ex -> {
                    NotFoundException nfe = (NotFoundException) ex;
                    Assertions.assertThat(nfe.getErrorCode()).isEqualTo(ErrorCode.EVENT_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("fail: 존재하지 않는 스트리머 ID로 closeEvent 호출 -> USER_NOT_FOUND")
    public void testCloseEventFailure_StreamerNotFound() {
        // given
        int invalidStreamerId = 9999;
        when(streamerRepository.findById(invalidStreamerId)).thenReturn(Optional.empty());

        // 이벤트는 존재한다고 가정
        Event event = createTestEvent();
        ReflectionTestUtils.setField(event, "id", 1);
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        // when, then
        Assertions.assertThatThrownBy(() -> eventService.closeEvent(invalidStreamerId, 1))
                .isInstanceOf(NotFoundException.class)
                .satisfies(ex -> {
                    NotFoundException nfe = (NotFoundException) ex;
                    Assertions.assertThat(nfe.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("fail: 다른 스트리머가 이벤트 마감 -> STORE_NOT_MATCH")
    public void testCloseEventFailure_StoreNotMatch() {
        // given
        // testStreamer != otherStreamer
        String tempSuffix = UUID.randomUUID().toString().substring(0, 4);
        Streamer otherStreamer = Streamer.builder()
                .name("다른스트리머" + tempSuffix)
                .nickName("다른스트리머" + tempSuffix)
                .phoneNumber("01011111111")
                .email("other" + tempSuffix + "@example.com")
                .password("pw")
                .build();
        ReflectionTestUtils.setField(otherStreamer, "id", 2);

        // 다른 스트리머 소유 이벤트
        Store otherStore = Store.builder()
                .streamer(otherStreamer)
                .storeName("OtherStore" + tempSuffix)
                .address("OtherAddress" + tempSuffix)
                .registrationNumber("OtherReg" + tempSuffix)
                .build();
        ReflectionTestUtils.setField(otherStore, "id", 200);

        Event event = Event.builder()
                .store(otherStore)
                .numberOfWinners(3)
                .participantCondition(ParticipantCondition.ALL_USERS)
                .selectionMethod(SelectionMethod.FIRST_COME_FIRST_SERVED)
                .startDatetime(LocalDateTime.now().minusDays(1))
                .endDatetime(LocalDateTime.now().plusDays(1))
                .generatedCode("OTHER_CODE")
                .build();
        ReflectionTestUtils.setField(event, "id", 99);

        when(streamerRepository.findById(testStreamer.getId())).thenReturn(Optional.of(testStreamer));
        when(eventRepository.findById(99)).thenReturn(Optional.of(event));

        // when, then
        Assertions.assertThatThrownBy(() -> eventService.closeEvent(testStreamer.getId(), 99))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(ex -> {
                    UnauthorizedException ue = (UnauthorizedException) ex;
                    Assertions.assertThat(ue.getErrorCode()).isEqualTo(ErrorCode.STORE_NOT_MATCH);
                });
    }

    @Test
    @DisplayName("fail: 이미 취소된 이벤트 마감 -> EVENT_ALREADY_CANCELED")
    public void testCloseEventFailure_AlreadyCanceled() {
        // given
        Event event = createTestEvent();
        ReflectionTestUtils.setField(event, "id", 1);
        ReflectionTestUtils.setField(event, "isDeleted", true);  // 이미 취소된 상태

        when(streamerRepository.findById(testStreamer.getId())).thenReturn(Optional.of(testStreamer));
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        // when, then
        Assertions.assertThatThrownBy(() -> eventService.closeEvent(testStreamer.getId(), 1))
                .isInstanceOf(BadRequestException.class)
                .satisfies(ex -> {
                    BadRequestException be = (BadRequestException) ex;
                    Assertions.assertThat(be.getErrorCode()).isEqualTo(ErrorCode.EVENT_ALREADY_CANCELED);
                });
    }

    @Test
    @DisplayName("fail: 이미 마감된 이벤트 마감 -> EVENT_ALREADY_CLOSED")
    public void testCloseEventFailure_AlreadyClosed() {
        // given
        Event event = createTestEvent();
        ReflectionTestUtils.setField(event, "id", 2);
        ReflectionTestUtils.setField(event, "isCompleted", true); // 이미 마감된 상태

        when(streamerRepository.findById(testStreamer.getId())).thenReturn(Optional.of(testStreamer));
        when(eventRepository.findById(2)).thenReturn(Optional.of(event));

        // when, then
        Assertions.assertThatThrownBy(() -> eventService.closeEvent(testStreamer.getId(), 2))
                .isInstanceOf(BadRequestException.class)
                .satisfies(ex -> {
                    BadRequestException be = (BadRequestException) ex;
                    Assertions.assertThat(be.getErrorCode()).isEqualTo(ErrorCode.EVENT_ALREADY_CLOSED);
                });
    }

// --------------------4. cancelEvent 테스트--------------------

    @Test
    @DisplayName("success: 이벤트 취소")
    public void testCancelEventSuccess() {
        // given
        // 이벤트 생성 시 차감될 재고 계산을 위해 EventItemRequestDto 준비
        List<EventItemRequestDto> eventItemList = new ArrayList<>();

        eventItemList.add(new EventItemRequestDto(testItem1.getId(), 2));


        EventRequestDto requestDto = EventRequestDto.builder()
                .storeId(testStore.getId())
                .numberOfWinners(3)
                .participantCondition(ParticipantCondition.ALL_USERS.name())
                .selectionMethod(SelectionMethod.RANDOM_DRAW.name())
                .startDatetime(LocalDateTime.now().minusMinutes(1))
                .endDatetime(LocalDateTime.now().plusDays(1))
                .eventItemList(eventItemList)
                .build();
        // stubbing: store, item, 이벤트 저장
        when(storeRepository.findById(testStore.getId())).thenReturn(Optional.of(testStore));
        when(itemRepository.findById(testItem1.getId())).thenReturn(Optional.of(testItem1));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            ReflectionTestUtils.setField(event, "id", 1);
            return event;
        });
        when(eventItemRepository.save(any(EventItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponseDto createdEvent = eventService.createEvent(testStreamer.getId(), requestDto);
        // 취소 전 상태: isDeleted false, 아이템 재고는 testItem1.remainingQuantity==100
        Event event = createTestEvent();
        ReflectionTestUtils.setField(event, "id", createdEvent.getId());
        ReflectionTestUtils.setField(event, "isDeleted", false);
        EventItem eventItem = new EventItem(event, testItem1, 2);
        ReflectionTestUtils.setField(eventItem, "id", 10);

        when(eventRepository.findById(createdEvent.getId())).thenReturn(Optional.of(event));
        when(eventItemRepository.findByEvent(event)).thenReturn(List.of(eventItem));

        // when
        eventService.cancelEvent(testStreamer.getId(), createdEvent.getId());

        // then: 이벤트 취소 후 상태 업데이트 검증 (isDeleted true)
        Assertions.assertThat(event.getIsDeleted()).isTrue();
        // 재고 복구: 테스트에서는 testItem1.remainingQuantity 원래 100 값으로 복구된다고 가정
        when(itemRepository.findById(testItem1.getId())).thenReturn(Optional.of(testItem1));
        Item itemReloaded = itemRepository.findById(testItem1.getId()).orElse(null);
        Assertions.assertThat(itemReloaded.getRemainingQuantity()).isEqualTo(100);
    }

    @Test
    @DisplayName("fail: 마감된 이벤트 취소 시")
    public void testCancelEventFailure_AlreadyCompleted() {
        // given
        Event event = createTestEvent();
        ReflectionTestUtils.setField(event, "id", 1);
        ReflectionTestUtils.setField(event, "isCompleted", true);
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(streamerRepository.findById(event.getId())).thenReturn(Optional.of(testStreamer));


        // when, then
        Assertions.assertThatThrownBy(() -> {
                    eventService.cancelEvent(testStreamer.getId(), 1);
                })
                .isInstanceOf(BadRequestException.class)
                .satisfies(ex -> {
                    BadRequestException badRequestException = (BadRequestException) ex;
                    Assertions.assertThat(badRequestException.getErrorCode()).isEqualTo(ErrorCode.EVENT_ALREADY_CLOSED);
                });
    }

    @Test
    @DisplayName("fail: 존재하지 않는 이벤트 취소 시 -> EVENT_NOT_FOUND")
    public void testCancelEventFailure_EventNotFound() {
        // given
        when(eventRepository.findById(-999)).thenReturn(Optional.empty()); // 잘못된 이벤트 ID

        // when, then
        Assertions.assertThatThrownBy(() -> eventService.cancelEvent(testStreamer.getId(), -999))
                .isInstanceOf(NotFoundException.class)
                .satisfies(ex -> {
                    NotFoundException nfe = (NotFoundException) ex;
                    Assertions.assertThat(nfe.getErrorCode()).isEqualTo(ErrorCode.EVENT_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("fail: 존재하지 않는 스트리머 ID로 이벤트 취소 시 -> USER_NOT_FOUND")
    public void testCancelEventFailure_StreamerNotFound() {
        // given
        int invalidStreamerId = 9999;
        when(streamerRepository.findById(invalidStreamerId)).thenReturn(Optional.empty());

        // 이벤트도 임시로 존재한다고 가정
        Event event = createTestEvent();
        ReflectionTestUtils.setField(event, "id", 1);
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        // when, then
        Assertions.assertThatThrownBy(() -> eventService.cancelEvent(invalidStreamerId, 1))
                .isInstanceOf(NotFoundException.class)
                .satisfies(ex -> {
                    NotFoundException nfe = (NotFoundException) ex;
                    Assertions.assertThat(nfe.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }


    // --------------------7. applyForEvent 테스트--------------------
    @Test
    void testApplyForEventRandomDraw_Success() {
        // given
        EventApplicantRequestDto requestDto = EventApplicantRequestDto.builder()
                .email("test@example.com")
                .applicantName("테스트사용자")
                .applicantPassword("password")
                .deliveryAddress("주소")
                .build();
        Event testEvent = createTestEvent();
        ReflectionTestUtils.setField(testEvent, "selectionMethod", SelectionMethod.RANDOM_DRAW);
        String testCode = testEvent.getGeneratedCode();

        when(eventRepository.findByGeneratedCode(testCode)).thenReturn(Optional.of(testEvent));

        when(eventApplicantRepository.existsByEventAndEmail(eq(testEvent), eq(requestDto.getEmail()))).thenReturn(false);

        when(eventApplicantRepository.save(any(EventApplicant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

// when
        EventApplicantResponseDto result = eventApplicantService.applyForEvent(testCode, requestDto);

// then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getApplicantStatus()).isEqualTo(ApplicantStatus.WAITING);
    }

    @Test
    @DisplayName("(2') applyForEvent - FIRST_COME_FIRST_SERVED - LOSER (정원 초과)")
    void testApplyForEventFirstCome_Success_Loser() {
        // given
        testEvent = createTestEvent(SelectionMethod.FIRST_COME_FIRST_SERVED, false, false);
        // numberOfWinners=3이고 이미 3명 WINNER 존재
        when(eventRepository.findByGeneratedCode(testCode))
                .thenReturn(Optional.of(testEvent));
        when(eventApplicantRepository.existsByEventAndEmail(eq(testEvent), eq(testRequestDto.getEmail())))
                .thenReturn(false);
        when(eventApplicantRepository.countByEventAndApplicantStatus(eq(testEvent), eq(ApplicantStatus.WINNER)))
                .thenReturn(3);

        when(eventApplicantRepository.save(any(EventApplicant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        EventApplicantResponseDto result = eventApplicantService.applyForEvent(testCode, testRequestDto);

        // then
        Assertions.assertThat(result.getApplicantStatus()).isEqualTo(ApplicantStatus.LOSER);
    }

    /**
     * (3) 예외 케이스 - GeneratedCode로 이벤트 찾을 수 없음
     */
    @Test
    @DisplayName("(3) applyForEvent - EVENT_NOT_FOUND")
    void testApplyForEvent_EventNotFound() {
        // given
        when(eventRepository.findByGeneratedCode(testCode))
                .thenReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() ->
                        eventApplicantService.applyForEvent(testCode, testRequestDto))
                .isInstanceOf(NotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EVENT_NOT_FOUND);
    }

    /**
     * (4) 예외 케이스 - 이벤트가 이미 마감(isCompleted=true)
     */
    @Test
    @DisplayName("(4) applyForEvent - EVENT_ALREADY_CLOSED")
    void testApplyForEvent_AlreadyClosed() {
        // given
        ReflectionTestUtils.setField(testEvent, "isCompleted", true);
        when(eventRepository.findByGeneratedCode(testCode))
                .thenReturn(Optional.of(testEvent));

        // when & then
        Assertions.assertThatThrownBy(() ->
                        eventApplicantService.applyForEvent(testCode, testRequestDto))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EVENT_ALREADY_CLOSED);
    }

    /**
     * (5) 예외 케이스 - 이벤트가 이미 취소(isDeleted=true)
     */
    @Test
    @DisplayName("(5) applyForEvent - EVENT_ALREADY_CANCELED")
    void testApplyForEvent_AlreadyCanceled() {
        // given
        ReflectionTestUtils.setField(testEvent, "isDeleted", true);
        when(eventRepository.findByGeneratedCode(testCode))
                .thenReturn(Optional.of(testEvent));

        // when & then
        Assertions.assertThatThrownBy(() ->
                        eventApplicantService.applyForEvent(testCode, testRequestDto))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EVENT_ALREADY_CANCELED);
    }

    /**
     * (6) 예외 케이스 - 이벤트 시작 전(now < startDatetime)
     */
    @Test
    @DisplayName("(6) applyForEvent - EVENT_APPLICANT_NOT_STARTED")
    void testApplyForEvent_NotStarted() {
        // given
        // 지금보다 미래 시간에 시작하도록 설정
        ReflectionTestUtils.setField(testEvent, "startDatetime", LocalDateTime.now().plusHours(1));
        when(eventRepository.findByGeneratedCode(testCode))
                .thenReturn(Optional.of(testEvent));

        // when & then
        Assertions.assertThatThrownBy(() ->
                        eventApplicantService.applyForEvent(testCode, testRequestDto))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EVENT_APPLICANT_NOT_STARTED);
    }

    /**
     * (7) 예외 케이스 - 이벤트가 이미 종료(now > endDatetime)
     */
    @Test
    @DisplayName("(7) applyForEvent - EVENT_APPLICANT_EXPIRED")
    void testApplyForEvent_Expired() {
        // given
        // 이미 과거 끝난 시간
        ReflectionTestUtils.setField(testEvent, "endDatetime", LocalDateTime.now().minusHours(1));
        when(eventRepository.findByGeneratedCode(testCode))
                .thenReturn(Optional.of(testEvent));

        // when & then
        Assertions.assertThatThrownBy(() ->
                        eventApplicantService.applyForEvent(testCode, testRequestDto))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EVENT_APPLICANT_EXPIRED);
    }

    /**
     * (8) 예외 케이스 - 이미 같은 Email로 중복 지원
     */
    @Test
    @DisplayName("(8) applyForEvent - EMAIL_ALREADY_EXISTS")
    void testApplyForEvent_EmailAlreadyExists() {
        // given
        when(eventRepository.findByGeneratedCode(testCode))
                .thenReturn(Optional.of(testEvent));
        // 이미 같은 email로 EventApplicant가 있다고 가정
        when(eventApplicantRepository.existsByEventAndEmail(eq(testEvent), eq(testRequestDto.getEmail())))
                .thenReturn(true);

        // when & then
        Assertions.assertThatThrownBy(() ->
                        eventApplicantService.applyForEvent(testCode, testRequestDto))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    }



// --------------------6. confirmReceipt 테스트--------------------

    @Test
    @DisplayName("success: 당첨자 수령 확정")
    public void testConfirmReceiptSuccess() {
        // given
        Event event = createTestEvent();
        ReflectionTestUtils.setField(event, "id", 1);
        // stubbing: 이벤트 조회 (상세 stubbing 생략)
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        EventApplicant applicant = EventApplicant.builder()
                .event(event)
                .email("confirm@example.com")
                .applicantName("지원자")
                .applicantPassword("password")
                .deliveryAddress("기존주소")
                .applicantStatus(ApplicantStatus.WINNER) // 당첨 상태
                .build();
        ReflectionTestUtils.setField(applicant, "id", 1);
        when(eventApplicantRepository.findById(1)).thenReturn(Optional.of(applicant));
        // confirmReceipt 후 저장 stubbing
        when(eventApplicantRepository.save(any(EventApplicant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventApplicantRequestDto updateRequest = EventApplicantRequestDto.builder()
                .applicantName("변경된이름")
                .deliveryAddress("변경된주소")
                .build();

        // when
        EventApplicantResponseDto updatedResponse = eventApplicantService.confirmReceipt(1, updateRequest);

        // then
        Assertions.assertThat(updatedResponse).isNotNull();
        Assertions.assertThat(updatedResponse.getId()).isEqualTo(1);
        Assertions.assertThat(updatedResponse.getApplicantName()).isEqualTo("변경된이름");
        Assertions.assertThat(updatedResponse.getDeliveryAddress()).isEqualTo("변경된주소");
        Assertions.assertThat(updatedResponse.getApplicantStatus()).isEqualTo(ApplicantStatus.CONFIRMED);
    }

    @Test
    @DisplayName("fail: 당첨이 아닌 지원자가 수령 확정 시")
    public void testConfirmReceiptFailure_NotWinner() {
        // given
        Event event = createTestEvent();
        ReflectionTestUtils.setField(event, "id", 1);
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        EventApplicant applicant = EventApplicant.builder()
                .event(event)
                .email("nonWinner@example.com")
                .applicantName("지원자")
                .applicantPassword("password")
                .deliveryAddress("주소")
                .applicantStatus(ApplicantStatus.WAITING) // 당첨 아님
                .build();
        ReflectionTestUtils.setField(applicant, "id", 2);
        when(eventApplicantRepository.findById(2)).thenReturn(Optional.of(applicant));

        EventApplicantRequestDto updateRequest = EventApplicantRequestDto.builder()
                .applicantName("이름변경")
                .deliveryAddress("주소변경")
                .build();

        // when, then
        Assertions.assertThatThrownBy(() -> {
                    eventApplicantService.confirmReceipt(2, updateRequest);
                })
                .isInstanceOf(BadRequestException.class)
                .satisfies(ex -> {
                    BadRequestException be = (BadRequestException) ex;
                    Assertions.assertThat(be.getErrorCode()).isEqualTo(ErrorCode.EVENT_APPLICANT_NOT_WINNER);
                });
    }


    private Event createTestEvent() {
        return createTestEvent(SelectionMethod.FIRST_COME_FIRST_SERVED, false, false);
    }

    private Event createTestEvent(SelectionMethod selectionMethod, boolean isCompleted, boolean isDeleted) {
        Event event = Event.builder()
                .store(testStore) // 실제 테스트에서는 Store 세팅
                .numberOfWinners(3)
                .generatedCode(testCode)
                .participantCondition(ParticipantCondition.ALL_USERS)
                .selectionMethod(selectionMethod)
                .startDatetime(LocalDateTime.now().minusHours(1)) // 이미 시작된 상태
                .endDatetime(LocalDateTime.now().plusHours(1))    // 아직 끝나지 않은 상태
                .build();
        ReflectionTestUtils.setField(event, "isCompleted", isCompleted);
        ReflectionTestUtils.setField(event, "isDeleted", isDeleted);
        return event;
    }

}
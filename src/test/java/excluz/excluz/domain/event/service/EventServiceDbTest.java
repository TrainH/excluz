package excluz.excluz.domain.event.service;

import excluz.excluz.common.entity.*;
import excluz.excluz.common.exception.NotFoundException;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EventServiceDbTest {
    @Autowired
    private StreamerRepository streamerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventItemRepository eventItemRepository;

    @Autowired
    private EventApplicantRepository eventApplicantRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventApplicantService eventApplicantService;

    private Streamer testStreamer;
    private Store testStore;
    private Item testItem1;
    private Item testItem2;
    private String uniqueSuffix;

    @BeforeEach
    public void setup() {
        // 각 테스트마다 중복데이터 방지를 위해 고유 접미사 사용
        uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);

        testStreamer = Streamer.builder()
                .name("스트리머" + uniqueSuffix)
                .nickName("스트리머" + uniqueSuffix)
                .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
                .email("streamer" + uniqueSuffix + "@example.com")
                .password("password")
                .build();
        streamerRepository.save(testStreamer);

        Store store = Store.builder()
                .streamer(testStreamer)
                .storeName("StoreName" + uniqueSuffix)
                .address("StoreAddress" + uniqueSuffix)
                .registrationNumber("RegNum" + uniqueSuffix)
                .build();
        testStore = storeRepository.save(store);

        Item item1 = Item.builder()
                .store(testStore)
                .itemName("TestItem" + uniqueSuffix + "1")
                .explanation("Description")
                .price(5000)
                .remainingQuantity(100)
                .build();
        testItem1 = itemRepository.save(item1);

        Item item2 = Item.builder()
                .store(testStore)
                .itemName("TestItem" + uniqueSuffix + "2")
                .explanation("Description")
                .price(10000)
                .remainingQuantity(100)
                .build();
        testItem2 = itemRepository.save(item2);
    }

    // --------------------1. createEvent 테스트--------------------
    @Test
    public void testCreateEventSuccess() {
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

        EventResponseDto responseDto = eventService.createEvent(testStreamer.getId(), requestDto);

        assertNotNull(responseDto);
        assertNotNull(responseDto.getId());
        assertEquals(testStore.getId(), responseDto.getStreamerStoreId());
        assertEquals(3, responseDto.getNumberOfWinners());
        assertFalse(responseDto.getIsCompleted());
        assertEquals(ParticipantCondition.ALL_USERS.name(), responseDto.getParticipantCondition());
        assertEquals(SelectionMethod.RANDOM_DRAW.name(), responseDto.getSelectionMethod());
        assertNotNull(responseDto.getGeneratedCode());
        assertNotNull(responseDto.getEventItemList());
        assertEquals(2, responseDto.getEventItemList().size());

        // DB 저장 여부 확인
        Optional<Event> eventOptional = eventRepository.findById(responseDto.getId());
        assertTrue(eventOptional.isPresent());
        Event event = eventOptional.get();
        List<EventItem> eventItems = eventItemRepository.findByEvent(event);
        assertEquals(2, eventItems.size());
        // 아이템 수량 및 연관 Item 체크
        EventItem eventItem = eventItems.get(0);
        assertTrue(eventItem.getItem().getId().equals(testItem1.getId()) || eventItem.getItem().getId().equals(testItem2.getId()));
    }

    // 1-2. 잘못된 ID로 create
    @Test
    public void testCreateEventFailure_InvalidStore() {
        List<EventItemRequestDto> eventItemList = new ArrayList<>();
        eventItemList.add(new EventItemRequestDto(testItem1.getId(), 5));

        EventRequestDto requestDto = EventRequestDto.builder()
                .storeId(-999)   // 잘못된 스토어 ID
                .numberOfWinners(3)
                .participantCondition(ParticipantCondition.ALL_USERS.name())
                .selectionMethod(SelectionMethod.RANDOM_DRAW.name())
                .startDatetime(LocalDateTime.now().minusMinutes(1))
                .endDatetime(LocalDateTime.now().plusDays(1))
                .eventItemList(eventItemList)
                .build();

        when(storeRepository.findById(-999)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> {
                    eventService.createEvent(testStreamer.getId(), requestDto);
                })
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ErrorCode.STORE_NOT_FOUND.getMessage());
}


// 1-3. 잘못된 ID로 create
@Test
public void testCreateEventFailure_ItemNotBelongToStore() {
    String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);
    Streamer streamer2 = Streamer.builder()
            .name("스트리머" + uniqueSuffix)
            .nickName("스트리머" + uniqueSuffix)
            .phoneNumber("010" + ((int) (Math.random() * 90000000 + 10000000)))
            .email("streamer2" + uniqueSuffix + "@example.com")
            .password("password")
            .build();
    streamerRepository.save(streamer2);

    Store otherStore = Store.builder()
            .streamer(streamer2)
            .storeName("OtherStore" + uniqueSuffix)
            .address("OtherAddress" + uniqueSuffix)
            .registrationNumber("OtherReg" + uniqueSuffix)
            .build();
    otherStore = storeRepository.save(otherStore);

    Item otherItem = Item.builder()
            .store(otherStore)
            .itemName("OtherItem" + uniqueSuffix)
            .explanation("Other Desc")
            .price(7000)
            .remainingQuantity(50)
            .build();
    otherItem = itemRepository.save(otherItem);

    List<EventItemRequestDto> eventItemList = new ArrayList<>();
    // 기존 스토어에 속하지 않는 아이템 사용
    eventItemList.add(new EventItemRequestDto(otherItem.getId(), 2));

    EventRequestDto requestDto = EventRequestDto.builder()
            .storeId(testStore.getId())  // 기본 store
            .numberOfWinners(3)
            .participantCondition(ParticipantCondition.ALL_USERS.name())
            .selectionMethod(SelectionMethod.RANDOM_DRAW.name())
            .startDatetime(LocalDateTime.now().minusMinutes(1))
            .endDatetime(LocalDateTime.now().plusDays(1))
            .eventItemList(eventItemList)
            .build();

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        eventService.createEvent(testStreamer.getId(), requestDto);
    });
    assertTrue(exception.getMessage().contains("현재 스토어에 소속되어 있지 않습니다."));
}

// -------------------- 2. getAllEvents, getEvent 테스트 --------------------
@Test
public void testGetAllEventsSuccess() {
    createTestEvent();
    createTestEvent();

    List<EventResponseDto> eventList = eventService.getAllEvents();

    assertNotNull(eventList);
    assertTrue(eventList.size() >= 2);
    for (EventResponseDto eventResponseDto : eventList) {
        System.out.println("Event Id: " + eventResponseDto.getId());
        System.out.println("Streamer Id: " + eventResponseDto.getStreamerStoreId());
        System.out.println("ItemList: " + eventResponseDto.getEventItemList()); // 여러 건 조회에서는 Item들은 null 반환
        System.out.println();
    }
}

//   2-2. 단건 조회
@Test
public void testGetEventSuccess() {
    EventResponseDto createdEvent = createTestEvent();

    EventResponseDto fetchedEvent = eventService.getEvent(createdEvent.getId());

    assertNotNull(fetchedEvent);
    assertEquals(createdEvent.getId(), fetchedEvent.getId());
    assertNotNull(fetchedEvent.getEventItemList());
    assertFalse(fetchedEvent.getEventItemList().isEmpty());
    // 콘솔 출력 (디버깅용)
    System.out.println("Event ID : " + fetchedEvent.getId());
}

//  2-3. 존재하지 않는 조회
@Test
public void testGetEventFailure_NotFound() {
    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        eventService.getEvent(-999);
    });
    assertTrue(exception.getMessage().contains("해당 이벤트를 찾을 수 없습니다."));
}

// -------------------- 3. closeEvent 테스트 --------------------
@Test
public void testCloseEventSuccess() {
    EventResponseDto createdEvent = createTestEvent();
    Event event = eventRepository.findById(createdEvent.getId()).orElseThrow();

    int totalApplicants = 10;
    for (int i = 0; i < totalApplicants; i++) {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 3);
        EventApplicant applicant = EventApplicant.builder()
                .event(event)
                .email("applicant" + uniqueSuffix + "@example.com")
                .applicantName("지원자" + uniqueSuffix)
                .applicantPassword("password")
                .deliveryAddress("Address" + uniqueSuffix)
                .applicantStatus(ApplicantStatus.WAITING)
                .build();
        eventApplicantRepository.save(applicant);
    }

    EventClosingResponseDto closingResponse = eventService.closeEvent(testStreamer.getId(), createdEvent.getId());

    assertNotNull(closingResponse);
    assertEquals(createdEvent.getId(), closingResponse.getId());
    assertTrue(closingResponse.getIsCompleted());
    assertNotNull(closingResponse.getEventApplicants());
    assertEquals(totalApplicants, closingResponse.getEventApplicants().size());

    // 당첨자/낙첨자 상태 검증
    List<EventApplicant> applicants = eventApplicantRepository.findByEvent(event);
    int winnerCount = 0;
    for (EventApplicant a : applicants) {
        assertTrue(a.getApplicantStatus() == ApplicantStatus.WINNER ||
                a.getApplicantStatus() == ApplicantStatus.LOSER);
        if (a.getApplicantStatus() == ApplicantStatus.WINNER) {
            winnerCount++;
        }
    }
    assertEquals(createdEvent.getNumberOfWinners(), winnerCount);
}

//    3-2.이벤트에 응모자가 없는 경우
@Test
public void testCloseEventFailure_NoApplicants() {
    EventResponseDto createdEvent = createTestEvent();

    Exception exception = assertThrows(IllegalStateException.class, () -> {
        eventService.closeEvent(testStreamer.getId(), createdEvent.getId());
    });

    assertTrue(exception.getMessage().contains("응모자가 없습니다."));
}

// -------------------- 4. cancelEvent 테스트 --------------------
@Test
public void testCancelEventSuccess() {
    List<EventItemRequestDto> eventItemList = new ArrayList<>();
    eventItemList.add(new EventItemRequestDto(testItem1.getId(), 2)); // 차감될 재고:2*당첨자수
    EventRequestDto requestDto = EventRequestDto.builder()
            .storeId(testStore.getId())
            .numberOfWinners(3)
            .participantCondition(ParticipantCondition.ALL_USERS.name())
            .selectionMethod(SelectionMethod.RANDOM_DRAW.name())
            .startDatetime(LocalDateTime.now().minusMinutes(1))
            .endDatetime(LocalDateTime.now().plusDays(1))
            .eventItemList(eventItemList)
            .build();
    EventResponseDto createdEvent = eventService.createEvent(testStreamer.getId(), requestDto);

    eventService.cancelEvent(testStreamer.getId(), createdEvent.getId());

    Event cancelledEvent = eventRepository.findById(createdEvent.getId()).orElseThrow();
    assertTrue(cancelledEvent.getIsDeleted());

    // 재고 복구 여부 확인 : 차감된 재고 만큼 복구되어야 함.
    Item item1Reload = itemRepository.findById(testItem1.getId()).orElseThrow();
    // 원래 잔여수량 100 - (2*3) 차감 후 cancel 에서 복구되므로 다시 100이어야 함.
    assertEquals(100, item1Reload.getRemainingQuantity());
}

//4-2. 마감된 이벤트 취소 시도
@Test
public void testCancelEventFailure_AlreadyCompleted() {
    EventResponseDto createdEvent = createTestEvent();
    Event event = eventRepository.findById(createdEvent.getId()).orElseThrow();
    for (int i = 0; i < 10; i++) {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 3);
        EventApplicant applicant = EventApplicant.builder()
                .event(event)
                .email("cancelApplicant" + uniqueSuffix + "@example.com")
                .applicantName("지원자" + uniqueSuffix)
                .applicantPassword("password")
                .deliveryAddress("Address" + uniqueSuffix)
                .applicantStatus(ApplicantStatus.WAITING)
                .build();
        eventApplicantRepository.save(applicant);
    }

    eventService.closeEvent(testStreamer.getId(), createdEvent.getId());

    Exception ex = assertThrows(IllegalStateException.class, () -> {
        eventService.cancelEvent(testStreamer.getId(), createdEvent.getId());
    });
    assertTrue(ex.getMessage().contains("이미 마감된 이벤트는 취소할 수 없습니다."));
}

// -------------------- 5. confirmReceipt 테스트 --------------------
@Test
public void testConfirmReceiptSuccess() {
    EventResponseDto createdEvent = createTestEvent();
    Event event = eventRepository.findById(createdEvent.getId()).orElseThrow();

    String uniqueSuffix = UUID.randomUUID().toString().substring(0, 3);
    EventApplicant applicant = EventApplicant.builder()
            .event(event)
            .email("confirm" + uniqueSuffix + "@example.com")
            .applicantName("지원자" + uniqueSuffix)
            .applicantPassword("password")
            .deliveryAddress("기존주소" + uniqueSuffix)
            .applicantStatus(ApplicantStatus.WINNER) // WINNER 상태 세팅
            .build();
    EventApplicant savedApplicant = eventApplicantRepository.save(applicant);

    // “수령 확정” 요청 DTO 준비
    EventApplicantRequestDto updateRequest = EventApplicantRequestDto.builder()
            .applicantName("변경된이름")
            .deliveryAddress("변경된주소")
            .build();

    EventApplicantResponseDto updatedResponse = eventApplicantService.confirmReceipt(savedApplicant.getId(), updateRequest);

    assertNotNull(updatedResponse);
    assertEquals(savedApplicant.getId(), updatedResponse.getId());
    assertEquals("변경된이름", updatedResponse.getApplicantName());
    assertEquals("변경된주소", updatedResponse.getDeliveryAddress());
    assertEquals(ApplicantStatus.CONFIRMED, updatedResponse.getApplicantStatus());
}

//    5-2.당첨자 아닌데 수령 확정 시도
@Test
public void testConfirmReceiptFailure_NotWinner() {
    EventResponseDto createdEvent = createTestEvent();
    Event event = eventRepository.findById(createdEvent.getId()).orElseThrow();
    String uniqueSuffix = UUID.randomUUID().toString().substring(0, 3);
    EventApplicant applicant = EventApplicant.builder()
            .event(event)
            .email("nonWinner" + uniqueSuffix + "@example.com")
            .applicantName("지원자" + uniqueSuffix)
            .applicantPassword("password")
            .deliveryAddress("주소" + uniqueSuffix)
            .applicantStatus(ApplicantStatus.WAITING) // WINNER가 아님
            .build();
    EventApplicant savedApplicant = eventApplicantRepository.save(applicant);

    EventApplicantRequestDto updateRequest = EventApplicantRequestDto.builder()
            .applicantName("이름변경")
            .deliveryAddress("주소변경")
            .build();

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        eventApplicantService.confirmReceipt(savedApplicant.getId(), updateRequest);
    });
    assertTrue(exception.getMessage().contains("당첨(WINNER) 상태가 아닌 유저의 수령 확정은 불가능합니다."));
}


// 헬퍼 메서드: 테스트 이벤트 생성
private EventResponseDto createTestEvent() {
    List<EventItemRequestDto> eventItemList = new ArrayList<>();
    eventItemList.add(new EventItemRequestDto(testItem1.getId(), 10));
    eventItemList.add(new EventItemRequestDto(testItem2.getId(), 3));

    EventRequestDto eventRequestDto = EventRequestDto.builder()
            .storeId(testStore.getId())
            .numberOfWinners(5)
            .participantCondition(ParticipantCondition.ALL_USERS.name())
            .selectionMethod(SelectionMethod.RANDOM_DRAW.name())
            .startDatetime(LocalDateTime.now().minusMinutes(1))
            .endDatetime(LocalDateTime.now().plusDays(1))
            .eventItemList(eventItemList)
            .build();

    return eventService.createEvent(testStreamer.getId(), eventRequestDto);
}

}
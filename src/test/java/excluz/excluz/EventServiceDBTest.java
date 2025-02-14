package excluz.excluz;



import excluz.excluz.common.entity.*;
import excluz.excluz.domain.event.event.dto.EventClosingResponseDto;
import excluz.excluz.domain.event.event.dto.EventRequestDto;
import excluz.excluz.domain.event.event.dto.EventResponseDto;
import excluz.excluz.domain.event.event.repository.EventRepository;
import excluz.excluz.domain.event.event.service.EventService;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantRequestDto;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantResponseDto;
import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import excluz.excluz.domain.event.eventApplicant.repository.EventApplicantRepository;
import excluz.excluz.domain.event.eventApplicant.service.EventApplicantService;
import excluz.excluz.domain.event.eventItem.dto.EventItemDto;
import excluz.excluz.domain.event.eventItem.dto.EventItemRequestDto;
import excluz.excluz.domain.event.eventItem.repository.EventItemRepository;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EventServiceDBTest {


    @Autowired
    private StreamerRepository streamerRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventApplicantService eventApplicantService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventItemRepository eventItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EventApplicantRepository eventApplicantRepository;

    private Store testStore;
    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    public void setup() {
        // 고유한 접미사 생성
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 2);

        Streamer streamer = Streamer.builder()
                .name("스트리머" + uniqueSuffix)
                .nickName("스트리머" + uniqueSuffix)
                .phoneNumber("010" + (int)(Math.random()*90000000 + 10000000))
                .email("streamer" + uniqueSuffix + "@example.com")
                .password("password")
                .build();
         streamerRepository.save(streamer);

        Store store = Store.builder()
                .streamer(streamer)
                .storeName("StoreName" + uniqueSuffix)
                .address("StoreAddress" + uniqueSuffix)
                .registrationNumber("RegNum" + uniqueSuffix)
                .build();
        testStore = storeRepository.save(store);

        Item item1 = Item.builder()
                .store(testStore) // Item은 Store를 참조합니다.
                .itemName("TestItem" + uniqueSuffix + 1)
                .explanation("Description")
                .price(5000)
                .remainingQuantity(100)
                .build();
        testItem1 = itemRepository.save(item1);

        Item item2 = Item.builder()
                .store(testStore) // Item은 Store를 참조합니다.
                .itemName("TestItem" + uniqueSuffix + 2)
                .explanation("Description")
                .price(10000)
                .remainingQuantity(100)
                .build();
        testItem2 = itemRepository.save(item2);

    }

    @Test
    public void testCreateEvent() {
        // Arrange
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);

        // EventItemRequestDto 준비
        List<EventItemRequestDto> eventItems = new ArrayList<>();
        EventItemRequestDto eventItemRequestDto = new EventItemRequestDto(testItem1.getId(), 10);
        eventItems.add(eventItemRequestDto);

        // EventRequestDto 준비
        EventRequestDto eventRequestDto = EventRequestDto.builder()
                .storeId(testStore.getId())
                .numberOfWinners(5)
                .participantCondition("ALL_USERS")
                .selectionMethod("RANDOM_DRAW")
                .startDatetime(LocalDateTime.now())
                .endDatetime(LocalDateTime.now().plusDays(1))
                .eventItems(eventItems)
                .build();

        // Act
        EventResponseDto eventResponseDto = eventService.createEvent(eventRequestDto);

        // Assert
        assertNotNull(eventResponseDto);
        assertNotNull(eventResponseDto.getId());
        assertEquals(eventResponseDto.getStreamerStoreId(), testStore.getId());
        assertEquals(eventResponseDto.getNumberOfWinners(), 5);
        assertFalse(eventResponseDto.getIsCompleted());
        assertEquals("ALL_USERS", eventResponseDto.getParticipantCondition());
        assertEquals("RANDOM_DRAW", eventResponseDto.getSelectionMethod());
        assertNotNull(eventResponseDto.getGeneratedCode());
        assertNotNull(eventResponseDto.getEventItems());
        assertEquals(1, eventResponseDto.getEventItems().size());

        // 데이터베이스에 데이터가 저장되었는지 확인
        Optional<Event> eventOptional = eventRepository.findById(eventResponseDto.getId());
        assertTrue(eventOptional.isPresent());
        Event event = eventOptional.get();
        assertEquals(event.getStore().getId(), testStore.getId());

        List<EventItem> eventItemList = eventItemRepository.findByEvent(event);
        assertEquals(1, eventItemList.size());
        EventItem eventItem = eventItemList.get(0);
        assertEquals(eventItem.getItem().getId(), testItem1.getId());
        assertEquals(eventItem.getQuantity(), 10);
    }

    @Test
    public void testGetAllEvents() {
        // Arrange
        // 이벤트 여러 개 생성
        createTestEvent();
        createTestEvent();

        // Act
        List<EventResponseDto> eventList = eventService.getAllEvents();

        // Assert
        assertNotNull(eventList);
        assertTrue(eventList.size() >=2); // 다른 테스트로 인해 이벤트가 더 있을 수 있음

    }

    @Test
    public void testGetEvent() {
        // Arrange
        EventResponseDto eventResponseDto = createTestEvent();

        // Act
        EventResponseDto fetchedEvent = eventService.getEvent(eventResponseDto.getId());

        // Assert
        assertNotNull(fetchedEvent);
        assertEquals(eventResponseDto.getId(), fetchedEvent.getId());
        assertEquals(eventResponseDto.getStreamerStoreId(), fetchedEvent.getStreamerStoreId());
        assertEquals(eventResponseDto.getNumberOfWinners(), fetchedEvent.getNumberOfWinners());

        assertNotNull(fetchedEvent.getEventItems(), "이벤트 아이템 목록이 null이면 안 됩니다.");
// createTestEvent()에서 아이템이 하나 이상 들어가는 경우가 있다면, 개수도 검사
        assertFalse(fetchedEvent.getEventItems().isEmpty(), "이벤트 아이템 목록이 비어 있으면 안 됩니다.");

// 콘솔 출력으로 확인
        System.out.println("========== [단건 조회 결과] ==========");
        System.out.println("Event ID : " + fetchedEvent.getId());
        System.out.println("Store ID : " + fetchedEvent.getStreamerStoreId());
        System.out.println("Number of Winners : " + fetchedEvent.getNumberOfWinners());
        System.out.println("Event Items : " + fetchedEvent.getEventItems().size());

// 각 아이템 정보도 출력해보기
        for (EventItemDto itemDto : fetchedEvent.getEventItems()) {
            System.out.println("Item ID           : " + itemDto.getId());
            System.out.println("Item Quantity     : " + itemDto.getQuantity());
            // 필요한 다른 필드도 있으면 추가 출력
        }
    }

    @Test
    public void testCloseEvent() {
        // Arrange
        EventResponseDto eventResponseDto = createTestEvent();

        // 응모자 생성
        Event event = eventRepository.findById(eventResponseDto.getId()).get();

        for(int i=0;i<10;i++){
            String uniqueSuffix = UUID.randomUUID().toString().substring(0, 3);

            EventApplicant applicant = EventApplicant.builder()
                    .event(event)
                    .email("applicant" + uniqueSuffix + "@example.com")
                    .applicantName("망금이" + uniqueSuffix)
                    .applicantPassword("password")
                    .deliveryAddress("Address" + uniqueSuffix)
                    .applicantStatus(ApplicantStatus.WAITING)
                    .build();
            eventApplicantRepository.save(applicant);
        }

        // Act
        EventClosingResponseDto closingResponse = eventService.closeEvent(eventResponseDto.getId());

        // Assert
        assertNotNull(closingResponse);
        assertEquals(eventResponseDto.getId(), closingResponse.getId());
        assertTrue(closingResponse.getIsCompleted());
        assertNotNull(closingResponse.getEventApplicants());
        assertEquals(10, closingResponse.getEventApplicants().size());

        // 응모자 상태가 WINNER 또는 LOSER로 업데이트되었는지 확인
        List<EventApplicant> applicants = eventApplicantRepository.findByEvent(event);
        int winnerCount = 0;
        for(EventApplicant applicant : applicants){
            assertTrue(applicant.getApplicantStatus() == ApplicantStatus.WINNER ||
                    applicant.getApplicantStatus() == ApplicantStatus.LOSER);
            if(applicant.getApplicantStatus() == ApplicantStatus.WINNER){
                winnerCount++;
            }
        }
        assertEquals(eventResponseDto.getNumberOfWinners(), winnerCount);
    }

    @Test
    public void testConfirmReceipt() {
        EventResponseDto eventDto = createTestEvent();

        Event event = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new IllegalStateException("이벤트 생성에 실패했습니다."));

        // 2) 응모자(EventApplicant) 하나 생성 (처음부터 WINNER로 세팅)
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 3);
        EventApplicant applicant = EventApplicant.builder()
                .event(event)
                .email("applicant" + uniqueSuffix + "@example.com")
                .applicantName("망금이" + uniqueSuffix)
                .applicantPassword("password123")
                .deliveryAddress("기존주소" + uniqueSuffix)
                .applicantStatus(ApplicantStatus.WINNER)  // ← 여기서 WINNER 상태로 생성
                .build();
        EventApplicant savedApplicant = eventApplicantRepository.save(applicant);

// 3) “수령 확정” 요청 DTO
        EventApplicantRequestDto updateRequest = EventApplicantRequestDto.builder()
                .applicantName("변경된이름")
                .deliveryAddress("변경된주소")
                .build();

// 4) 실제 “수령 확정” 메서드 호출
        EventApplicantResponseDto updatedResponse =
                eventApplicantService.confirmReceipt(savedApplicant.getId(), updateRequest);

// 5) 검증
        assertNotNull(updatedResponse);
        assertEquals(savedApplicant.getId(), updatedResponse.getId());
        assertEquals("변경된이름", updatedResponse.getApplicantName());
        assertEquals("변경된주소", updatedResponse.getDeliveryAddress());
        assertEquals(ApplicantStatus.CONFIRMED, updatedResponse.getApplicantStatus());
    }


    // 테스트 이벤트를 생성하는 헬퍼 메서드
    private EventResponseDto createTestEvent() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 3);

        // EventItemRequestDto 준비
        List<EventItemRequestDto> eventItems = new ArrayList<>();
        EventItemRequestDto eventItemRequestDto1 = new EventItemRequestDto(testItem1.getId(), 10);
        eventItems.add(eventItemRequestDto1);
        EventItemRequestDto eventItemRequestDto2 = new EventItemRequestDto(testItem2.getId(), 3);
        eventItems.add(eventItemRequestDto2);


        // EventRequestDto 준비
        EventRequestDto eventRequestDto = EventRequestDto.builder()
                .storeId(testStore.getId())
                .numberOfWinners(5)
                .participantCondition("ALL_USERS")
                .selectionMethod("RANDOM_DRAW")
                .startDatetime(LocalDateTime.now())
                .endDatetime(LocalDateTime.now().plusDays(1))
                .eventItems(eventItems)
                .build();

        return eventService.createEvent(eventRequestDto);
    }


}
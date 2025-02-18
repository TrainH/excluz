// package excluz.excluz;

// import excluz.excluz.common.entity.Event;
// import excluz.excluz.common.entity.Item;
// import excluz.excluz.common.entity.Store;
// import excluz.excluz.common.entity.Streamer;
// import excluz.excluz.domain.event.event.dto.EventRequestDto;
// import excluz.excluz.domain.event.event.dto.EventResponseDto;
// import excluz.excluz.domain.event.event.enums.ParticipantCondition;
// import excluz.excluz.domain.event.event.enums.SelectionMethod;
// import excluz.excluz.domain.event.event.repository.EventRepository;
// import excluz.excluz.domain.event.event.service.EventService;
// import excluz.excluz.domain.event.eventItem.dto.EventItemRequestDto;
// import excluz.excluz.domain.event.eventItem.repository.EventItemRepository;
// import excluz.excluz.domain.store.item.repository.ItemRepository;
// import excluz.excluz.domain.store.store.repository.StoreRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.time.LocalDateTime;
// import java.util.Collections;
// import java.util.Optional;

// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;

// @ExtendWith(MockitoExtension.class)
// class EventServiceTest {

//     @InjectMocks
//     private EventService eventService;

//     @Mock
//     private StoreRepository storeRepository;

//     @Mock
//     private EventRepository eventRepository;

//     @Mock
//     private EventItemRepository eventItemRepository;

//     @Mock
//     private ItemRepository itemRepository;

//     private Store store;
//     private Item item;
//     private Event event;

//     @BeforeEach
//     void setUp() {
//         // Store 객체 생성
//         store = Store.builder()
//                 .streamer(new Streamer())
//                 .address("Sample Address")
//                 .storeName("Sample Store")
//                 .registrationNumber("123-45-6789")
//                 .build();
//         store.setId(2);


//         // Item 객체 생성
//         item = new Item();
//         item.setId(1);
//         item.setItemName("Sample Item");

//         // Event 객체 생성
//         event = Event.builder()
//                 .store(store)
//                 .numberOfWinners(10)
//                 .participantCondition(ParticipantCondition.ALL_USERS)
//                 .selectionMethod(SelectionMethod.RANDOM_DRAW)
//                 .startDatetime(LocalDateTime.now())
//                 .endDatetime(LocalDateTime.now().plusDays(1))
//                 .generatedCode("UNIQUE_CODE")
//                 .build();
//         event.setId(2);
//     }

//     @Test
//     void createEvent_Success() {
//         // 준비

//         EventItemRequestDto eventItemRequestDto = new EventItemRequestDto(item.getId(), 5);

//         EventRequestDto eventRequestDto = EventRequestDto.builder()
//                 .storeId(store.getId())
//                 .numberOfWinners(10)
//                 .participantCondition("ALL_USERS")
//                 .selectionMethod("RANDOM_DRAW")
//                 .startDatetime(LocalDateTime.now())
//                 .endDatetime(LocalDateTime.now().plusDays(1))
//                 .eventItems(Collections.singletonList(eventItemRequestDto))
//                 .build();


//         when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
//         when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
//         when(eventRepository.save(any(Event.class))).thenReturn(event);
//         when(eventItemRepository.saveAll(anyList())).thenReturn(null);

//         // 실행
//         EventResponseDto responseDto = eventService.createEvent(eventRequestDto);

//         // 검증
//         assertNotNull(responseDto);
//         assertEquals(event.getId(), responseDto.getId());
//         assertEquals(event.getNumberOfWinners(), responseDto.getNumberOfWinners());
//         assertEquals(event.getParticipantCondition().name(), responseDto.getParticipantCondition());
//         assertEquals(event.getSelectionMethod().name(), responseDto.getSelectionMethod());

//         // 호출 검증
//         verify(storeRepository, times(1)).findById(store.getId());
//         System.out.println(storeRepository.findById(store.getId()));
//         verify(itemRepository, times(1)).findById(item.getId());
//         System.out.println(itemRepository.findById(item.getId()));
//         verify(eventRepository, times(1)).save(any(Event.class));
//         verify(eventItemRepository, times(1)).saveAll(anyList());
//     }

//     @Test
//     void createEvent_StoreNotFound() {
//         // 준비
//         EventRequestDto eventRequestDto = EventRequestDto.builder()
//                 .storeId(999) // 존재하지 않는 ID
//                 .numberOfWinners(10)
//                 .participantCondition("ALL_USERS")
//                 .selectionMethod("RANDOM_DRAW")
//                 .startDatetime(LocalDateTime.now())
//                 .endDatetime(LocalDateTime.now().plusDays(1))
//                 .eventItems(null)
//                 .build();

//         when(storeRepository.findById(999)).thenReturn(Optional.empty());

//         // 실행 및 검증
//         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//             eventService.createEvent(eventRequestDto);
//         });

//         assertEquals("해당 스토어를 찾을 수 없습니다.", exception.getMessage());
//         verify(storeRepository, times(1)).findById(999);
//     }

//     @Test
//     void createEvent_ItemNotFound() {
//         // 준비
//         EventItemRequestDto eventItemRequestDto = new EventItemRequestDto(999, 5);

//         EventRequestDto eventRequestDto = EventRequestDto.builder()
//                 .storeId(store.getId())
//                 .numberOfWinners(10)
//                 .participantCondition("ALL_USERS")
//                 .selectionMethod("RANDOM_DRAW")
//                 .startDatetime(LocalDateTime.now())
//                 .endDatetime(LocalDateTime.now().plusDays(1))
//                 .eventItems(Collections.singletonList(eventItemRequestDto))
//                 .build();

//         when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
//         when(itemRepository.findById(999)).thenReturn(Optional.empty());

//         // 실행 및 검증
//         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//             eventService.createEvent(eventRequestDto);
//         });

//         assertEquals("ID가 999인 아이템을 찾을 수 없습니다.", exception.getMessage());
//         verify(storeRepository, times(1)).findById(store.getId());
//         verify(itemRepository, times(1)).findById(999);
//     }

//     @Test
//     void createEvent_InvalidQuantity() {
//         // 준비
//         EventItemRequestDto eventItemRequestDto = new EventItemRequestDto(item.getId(), 0);


//         EventRequestDto eventRequestDto = EventRequestDto.builder()
//                 .storeId(store.getId())
//                 .numberOfWinners(10)
//                 .participantCondition("ALL_USERS")
//                 .selectionMethod("RANDOM_DRAW")
//                 .startDatetime(LocalDateTime.now())
//                 .endDatetime(LocalDateTime.now().plusDays(1))
//                 .eventItems(Collections.singletonList(eventItemRequestDto))
//                 .build();

//         when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
//         when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

//         // 실행 및 검증
//         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//             eventService.createEvent(eventRequestDto);
//         });

//         assertEquals("아이템 ID " + item.getId() + "의 수량은 0보다 커야 합니다.", exception.getMessage());
//         verify(storeRepository, times(1)).findById(store.getId());
//         verify(itemRepository, times(1)).findById(item.getId());
//     }

//     @Test
//     void createEvent_NoEventItems() {
//         // 준비
//         EventRequestDto eventRequestDto = EventRequestDto.builder()
//                 .storeId(store.getId())
//                 .numberOfWinners(10)
//                 .participantCondition("ALL_USERS")
//                 .selectionMethod("RANDOM_DRAW")
//                 .startDatetime(LocalDateTime.now())
//                 .endDatetime(LocalDateTime.now().plusDays(1))
//                 .eventItems(null) // 아이템 없이 이벤트 생성
//                 .build();

//         when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
//         when(eventRepository.save(any(Event.class))).thenReturn(event);

//         // 실행
//         var responseDto = eventService.createEvent(eventRequestDto);

//         // 검증
//         assertNotNull(responseDto);
//         assertEquals(event.getId(), responseDto.getId());
//         assertEquals(event.getNumberOfWinners(), responseDto.getNumberOfWinners());

//         // 호출 검증
//         verify(storeRepository, times(1)).findById(store.getId());
//         verify(eventRepository, times(1)).save(any(Event.class));
//         verify(eventItemRepository, never()).saveAll(anyList());
//         verify(itemRepository, never()).findById(anyInt());
//     }
// }

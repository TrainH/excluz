package excluz.excluz.domain.event.event.service;

import excluz.excluz.common.entity.EventItem;
import excluz.excluz.common.entity.Item;
import excluz.excluz.domain.event.eventItem.dto.EventItemRequestDto;
import excluz.excluz.domain.event.eventItem.repository.EventItemRepository;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.Store;
import excluz.excluz.domain.event.event.dto.EventRequestDto;
import excluz.excluz.domain.event.event.enums.ParticipantCondition;
import excluz.excluz.domain.event.event.enums.SelectionMethod;
import excluz.excluz.domain.event.event.repository.EventRepository;
import excluz.excluz.domain.event.event.dto.EventResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    private final StoreRepository storeRepository;
    private final EventRepository eventRepository;
    private final EventItemRepository eventItemRepository;
    private final ItemRepository itemRepository;


    public EventResponseDto createEvent(EventRequestDto eventRequestDto) {
        // 이벤트 생성 비즈니스 로직 구현
        Store store = storeRepository.findById(eventRequestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("해당 스토어를 찾을 수 없습니다."));

        // Event 엔티티 생성
        Event event = Event.builder()
                .store(store)
                .numberOfWinners(eventRequestDto.getNumberOfWinners())
                .participantCondition(ParticipantCondition.valueOf(eventRequestDto.getParticipantCondition()))
                .selectionMethod(SelectionMethod.valueOf(eventRequestDto.getSelectionMethod()))
                .startDatetime(eventRequestDto.getStartDatetime())
                .endDatetime(eventRequestDto.getEndDatetime())
                .isCompleted(false) // 초기값 설정
                .generatedCode(generateUniqueCode()) // 고유 코드 생성 로직 필요
                .build();

        // 이벤트 저장
        Event savedEvent = eventRepository.save(event);

        // todo: EventItems 관련 로직 정비중..
        List<EventItem> eventItems = null; // 생성 시 현재는 null로 처리

        if (eventRequestDto.getEventItems() != null && !eventRequestDto.getEventItems().isEmpty()) {
            eventItems = new ArrayList<>();

            for (EventItemRequestDto eventItemRequestDto : eventRequestDto.getEventItems()) {
                Integer itemId = eventItemRequestDto.getItemId();

                // Item 존재 여부 확인
                Item item = itemRepository.findById(itemId)
                        .orElseThrow(() -> new IllegalArgumentException("ID가 " + itemId + "인 아이템을 찾을 수 없습니다."));

                // 수량 검증
                Integer quantity = eventItemRequestDto.getQuantity();
                if (quantity == null || quantity <= 0) {
                    throw new IllegalArgumentException("아이템 ID " + itemId + "의 수량은 0보다 커야 합니다.");
                }

                // EventItem 생성
                EventItem eventItem = new EventItem(savedEvent, item, quantity);
                eventItems.add(eventItem);
            }

            // EventItems 저장
            eventItemRepository.saveAll(eventItems);
        }

        // EventResponseDto로 변환하여 반환
        return EventResponseDto.fromWithItems(savedEvent, eventItems);
    }


    // 이벤트 전체 조회 서비스 로직
    public List<EventResponseDto> getAllEvents() {
        // 이벤트 전체 조회
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(EventResponseDto::fromWithoutItems)
                .collect(Collectors.toList());
    }

    // 이벤트 단건 조회 서비스 로직
    public EventResponseDto getEvent(Integer eventId) {
        // 이벤트 단건 조회
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트를 찾을 수 없습니다. ID: " + eventId));

        // EventResponseDto로 변환하여 반환
        List<EventItem> eventItems = eventItemRepository.findByEvent(event);

        return EventResponseDto.fromWithItems(event, eventItems);
    }

    private String generateUniqueCode() {
        // 고유 코드 생성 로직 구현
        return "UNIQUE_CODE";
    }

}

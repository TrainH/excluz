package excluz.excluz.domain.event.event.service;

import excluz.excluz.common.entity.*;
import excluz.excluz.domain.event.event.dto.EventClosingResponseDto;
import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import excluz.excluz.domain.event.eventApplicant.repository.EventApplicantRepository;
import excluz.excluz.domain.event.eventItem.dto.EventItemRequestDto;
import excluz.excluz.domain.event.eventItem.repository.EventItemRepository;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
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
import java.util.Collections;
import java.util.Comparator;
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
    private final EventApplicantRepository eventApplicantRepository;

    //todo: 추후 temp 수정
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
            return EventResponseDto.fromWithItems(savedEvent, eventItems);
        } else {
            return EventResponseDto.fromWithoutItems(savedEvent);
        }

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

    // 이벤트 마감 메서드 추가
    public EventClosingResponseDto closeEvent(Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트를 찾을 수 없습니다. ID: " + eventId));

        if (event.getIsCompleted()) {
            throw new IllegalStateException("이미 마감된 이벤트입니다.");
        }

        // 이벤트 응모자 조회
        List<EventApplicant> applicantList = eventApplicantRepository.findByEvent(event);

        if (applicantList.isEmpty()) {
            throw new IllegalStateException("해당 이벤트에 응모자가 없습니다.");
        }

        // 당첨자 선정 로직
        int numberOfWinners = event.getNumberOfWinners();
        if (event.getSelectionMethod() == SelectionMethod.RANDOM_DRAW) {
            // 무작위 추첨
            Collections.shuffle(applicantList);
            List<EventApplicant> winners = applicantList.subList(0, Math.min(numberOfWinners, applicantList.size()));

            for (EventApplicant applicant : applicantList) {
                if (winners.contains(applicant)) {
                    applicant.updateApplicantStatus(ApplicantStatus.WINNER);
                } else {
                    applicant.updateApplicantStatus(ApplicantStatus.LOSER);
                }
            }
        } else if (event.getSelectionMethod() == SelectionMethod.FIRST_COME_FIRST_SERVED) {
            // 선착순
            applicantList.sort(Comparator.comparing(EventApplicant::getCreatedAt));
            List<EventApplicant> winners = applicantList.subList(0, Math.min(numberOfWinners, applicantList.size()));

            for (EventApplicant applicant : applicantList) {
                if (winners.contains(applicant)) {
                    applicant.updateApplicantStatus(ApplicantStatus.WINNER);
                } else {
                    applicant.updateApplicantStatus(ApplicantStatus.LOSER);
                }
            }
        } else {
            throw new UnsupportedOperationException("지원되지 않는 선정 방식입니다.");
        }

        event.completeEvent();

        // 변경사항 저장
        eventRepository.save(event);
        eventApplicantRepository.saveAll(applicantList);

        // EventItems 조회
        List<EventItem> eventItems = eventItemRepository.findByEvent(event);

        // 응답 DTO 생성
        EventClosingResponseDto eventClosingResponseDto = EventClosingResponseDto.from(event, eventItems, applicantList);

        return eventClosingResponseDto;
    }

    private String generateUniqueCode() {
        // 고유 코드 생성 로직 구현
        return "UNIQUE_CODE" + eventRepository.count();
    }



}

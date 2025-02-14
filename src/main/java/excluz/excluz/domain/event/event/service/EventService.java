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

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final StoreRepository storeRepository;
    private final EventRepository eventRepository;
    private final EventItemRepository eventItemRepository;
    private final ItemRepository itemRepository;
    private final EventApplicantRepository eventApplicantRepository;

    @Transactional
    public EventResponseDto createEvent(EventRequestDto eventRequestDto) {
        Store store = storeRepository.findById(eventRequestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("해당 스토어를 찾을 수 없습니다."));

        Event event = Event.builder()
                .store(store)
                .numberOfWinners(eventRequestDto.getNumberOfWinners())
                .participantCondition(ParticipantCondition.valueOf(eventRequestDto.getParticipantCondition()))
                .selectionMethod(SelectionMethod.valueOf(eventRequestDto.getSelectionMethod()))
                .startDatetime(eventRequestDto.getStartDatetime())
                .endDatetime(eventRequestDto.getEndDatetime())
                .generatedCode(generateUniqueCode())
                .build();

        Event savedEvent = eventRepository.save(event);

        List<EventItem> eventItems = null; // 생성 시 현재는 null로 처리
        Integer numberOfWinners = eventRequestDto.getNumberOfWinners();
        // todo: eventItem이 비어있는 경우 인 경우를 로직에서 제외하기 [  ]

        eventItems = new ArrayList<>();

        for (EventItemRequestDto eventItemRequestDto : eventRequestDto.getEventItemList()) {
            Integer itemId = eventItemRequestDto.getItemId();

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("ID가 " + itemId + "인 아이템을 찾을 수 없습니다."));

            if (item.getStore() == null || !item.getStore().getId().equals(store.getId())) {
                throw new IllegalArgumentException("아이템 ID " + itemId + "은(는) 현재 스토어에 소속되어 있지 않습니다.");
            }

            Integer requestQuantity = eventItemRequestDto.getQuantity();
            Integer remainingQuantity = item.getRemainingQuantity();
            if (requestQuantity > remainingQuantity) {
                throw new IllegalArgumentException("아이템 ID " + itemId + "의 요청 수량 " + requestQuantity +
                        "는 현재 남은 수량 " + remainingQuantity + "보다 많습니다.");
            }
            item.updateRemainingQuantity(remainingQuantity - requestQuantity);

            EventItem eventItem = new EventItem(savedEvent, item, requestQuantity);
            eventItems.add(eventItem);


        }

        eventItemRepository.saveAll(eventItems);
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

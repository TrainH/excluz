package excluz.excluz.domain.event.event.service;

import excluz.excluz.common.entity.*;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.UnauthorizedException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.event.event.dto.EventClosingResponseDto;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantResponseDto;
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
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final StreamerRepository streamerRepository;

    @Transactional
    public EventResponseDto createEvent(Integer streamerId, EventRequestDto eventRequestDto) {
        Store store = storeRepository.findById(eventRequestDto.getStoreId())
                .orElseThrow(() ->  new NotFoundException(ErrorCode.STORE_NOT_FOUND));

        Streamer streamer = streamerRepository.findById(streamerId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (!streamer.getId().equals(streamerId)){
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_USER);
        }

        if (eventRequestDto.getEndDatetime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(ErrorCode.EVENT_ENDDATETIME_TOO_EARLY);
        }

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

        List<EventItem> eventItemList = new ArrayList<>();

        for (EventItemRequestDto eventItemRequestDto : eventRequestDto.getEventItemList()) {
            Integer itemId = eventItemRequestDto.getItemId();

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND));

            if (item.getStore() == null || !item.getStore().getId().equals(store.getId())) {
                throw new UnauthorizedException(ErrorCode.ITEM_NOT_MATCH);
            }

            Integer requestedItemQuantity = eventItemRequestDto.getQuantity();
            item.removeRemainingQuantity(requestedItemQuantity * eventRequestDto.getNumberOfWinners());

            EventItem eventItem = new EventItem(savedEvent, item, requestedItemQuantity);
            eventItemList.add(eventItem);
        }

        eventItemRepository.saveAll(eventItemList);
        return EventResponseDto.fromWithItems(savedEvent, eventItemList);
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
                .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_NOT_FOUND));

        // EventResponseDto로 변환하여 반환
        List<EventItem> eventItems = eventItemRepository.findByEvent(event);

        return EventResponseDto.fromWithItems(event, eventItems);
    }

    // 이벤트 취소 시 재고 복구 및 이벤트 상태 변경 (예: 이벤트 취소 플래그 추가 등으로 확장 가능)
    @Transactional
    public void cancelEvent(Integer streamerId, Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_NOT_FOUND));

        Streamer streamer = streamerRepository.findById(streamerId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (!streamer.getId().equals(streamerId)){
            throw new UnauthorizedException(ErrorCode.STORE_NOT_MATCH);
        }

        if (event.getIsCompleted()) {
            throw new BadRequestException(ErrorCode.EVENT_ALREADY_CLOSED);
        }

        if (event.getIsDeleted()) {
            return;
        }

        // 이벤트의 종료 여부를 endDatetime과 현재 시간으로 재확인 (종료, 취소시에도 재고 복구)
        List<EventItem> eventItems = eventItemRepository.findByEvent(event);
        for (EventItem eventItem : eventItems) {
            Item item = eventItem.getItem();
            item.addRemainingQuantity(eventItem.getQuantity() * event.getNumberOfWinners());
        }

        event.updateIsDeleted(true);
        eventRepository.save(event);
    }

    // 이벤트 마감 메서드 추가
    public EventClosingResponseDto closeEvent(Integer streamerId, Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_NOT_FOUND));

        Streamer streamer = streamerRepository.findById(streamerId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (!streamer.getId().equals(streamerId)){
            throw new UnauthorizedException(ErrorCode.STORE_NOT_MATCH);
        }

        if (event.getIsDeleted()) {
            throw new BadRequestException(ErrorCode.EVENT_ALREADY_CANCELED);
        }

        if (event.getIsCompleted()) {
            throw new BadRequestException(ErrorCode.EVENT_ALREADY_CLOSED);
        }

        // 이벤트 응모자 조회
        List<EventApplicant> applicantList = eventApplicantRepository.findByEvent(event);
//        if (applicantList.isEmpty()) {
//            throw new IllegalStateException("해당 이벤트에 응모자가 없습니다.");
//        }

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
//      응모시에 이미 WINNER/LOSER 구분 완료
        } else {
            throw new BadRequestException(ErrorCode.EVENT_METHOD_NOT_SUPPORTED);
        }

        event.completeEvent();

        eventRepository.save(event);
        eventApplicantRepository.saveAll(applicantList);

        List<EventItem> eventItems = eventItemRepository.findByEvent(event);

        return EventClosingResponseDto.from(event, eventItems, applicantList);
    }

    private String generateUniqueCode() {
        // todo: 고유 코드 생성 로직 구현 (예제용 코드)
        return "UNIQUE_CODE" + eventRepository.count();
    }


}

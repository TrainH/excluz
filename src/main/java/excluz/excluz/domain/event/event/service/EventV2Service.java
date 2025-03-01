package excluz.excluz.domain.event.event.service;

import excluz.excluz.domain.event.event.dto.response.EventResponseWithoutEventItemDto;
import excluz.excluz.domain.event.event.repository.EventV2Repository;
import excluz.excluz.domain.event.eventItem.repository.EventItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventV2Service {

    private final EventV2Repository eventV2Repository;
    private final EventItemRepository eventItemRepository;

    @Transactional(readOnly = true)
    public Page<EventResponseWithoutEventItemDto> getEventList(Integer streamerId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), size);

        return eventV2Repository.findDtoByStreamerId(streamerId, pageable);
    }

//    todo: 추후 기능 추가시 활용 예정
//    @Transactional(readOnly = true)
//    public EventWithApplicantListResponseDto getEvent(Integer streamerId, Integer eventId) {
//
//        Event event = eventV2Repository.findById(eventId)
//                .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_NOT_FOUND));
//
//        if (!event.getStore().getStreamer().getId().equals(streamerId)) {
//            throw new UnauthorizedException(ErrorCode.STORE_NOT_MATCH);
//        }
//
//        List<EventItem> eventItemList = eventItemRepository.findByEvent(event);
//        List<EventApplicant> eventApplicantList = eventApplicantRepository.findByEvent(event);
//
//        return EventWithApplicantListResponseDto.from(event, eventItemList, eventApplicantList);
//    }
//
}

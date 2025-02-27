package excluz.excluz.domain.event.event.service;

import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.EventItem;
import excluz.excluz.domain.event.event.dto.EventResponseWithEventItemDto;
import excluz.excluz.domain.event.event.dto.EventResponseWithoutEventItemDto;
import excluz.excluz.domain.event.event.repository.EventV2Repository;
import excluz.excluz.domain.event.event.repository.EventV2RepositoryCustom;
import excluz.excluz.domain.event.eventApplicant.repository.EventApplicantRepository;
import excluz.excluz.domain.event.eventItem.repository.EventItemRepository;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventV2Service {

    private final EventV2Repository eventV2Repository;
    private final EventItemRepository eventItemRepository;


    @Transactional(readOnly = true)
    public Page<EventResponseWithoutEventItemDto> getEventList(Integer streamerId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), size);

        // 변경: eventV2Repository.findByStreamerIdUsingQueryDsl(streamer.getId(), pageable);
        Page<Event> eventPage = eventV2Repository.findByStreamerIdUsingQueryDsl(streamerId, pageable);

        return eventPage.map(EventResponseWithoutEventItemDto::from);
    }

}

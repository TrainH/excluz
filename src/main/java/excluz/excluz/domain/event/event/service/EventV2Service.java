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

}

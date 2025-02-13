package excluz.excluz.domain.event.event.service;

import excluz.excluz.common.entity.Event;
import excluz.excluz.domain.event.event.dto.EventResponseDto;
import excluz.excluz.domain.event.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;


//    @Transactional
//    public EventResponseDto createEvent(Event event) {
//
//        return
//    }

}

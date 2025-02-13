package excluz.excluz.domain.event.event.controller;

import excluz.excluz.common.entity.Event;
import excluz.excluz.domain.event.event.dto.EventRequestDto;
import excluz.excluz.domain.event.event.dto.EventResponseDto;
import excluz.excluz.domain.event.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class  EventController {

    private final EventService eventService;

    // 예약 생성 API
    @PostMapping()
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventRequestDto eventRequestDto) {
//        todo: 유저인증로직
        EventResponseDto eventResponseDto = eventService.createEvent(eventRequestDto);
        return ResponseEntity.status(201).body(eventResponseDto);
    }
}

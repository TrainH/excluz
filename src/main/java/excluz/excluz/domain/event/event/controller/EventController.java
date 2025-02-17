package excluz.excluz.domain.event.event.controller;

import excluz.excluz.common.entity.Event;
import excluz.excluz.domain.event.event.dto.EventClosingResponseDto;
import excluz.excluz.domain.event.event.dto.EventRequestDto;
import excluz.excluz.domain.event.event.dto.EventResponseDto;
import excluz.excluz.domain.event.event.service.EventService;
import excluz.excluz.domain.user.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping()
    public ResponseEntity<EventResponseDto> createEvent(@AuthenticationPrincipal User user,
                                                        @Valid @RequestBody EventRequestDto eventRequestDto) {
//      todo: 로그인 된 스트리머 id 확인 및 검사
//        Integer userId = Integer.parseInt(user.getUsername());
//        UserRole userRole = user.getAuthorities();
//        EventResponseDto eventResponseDto = eventService.createEvent(userId, eventRequestDto);
        EventResponseDto eventResponseDto = eventService.createEvent(eventRequestDto);
        return ResponseEntity.status(201).body(eventResponseDto);
    }

    @GetMapping()
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        // todo: 유저인증로직
        List<EventResponseDto> eventResponseDtoList = eventService.getAllEvents();
        return ResponseEntity.ok(eventResponseDtoList);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEvent(@PathVariable Integer eventId) {
        // todo: 유저인증로직
        EventResponseDto eventResponseDto = eventService.getEvent(eventId);
        return ResponseEntity.ok(eventResponseDto);
    }

    @PatchMapping("/{eventId}/applicants")
    public ResponseEntity<EventClosingResponseDto> closeEvent(@PathVariable Integer eventId) {
        // todo: 유저인증로직
        EventClosingResponseDto eventClosingResponseDto = eventService.closeEvent(eventId);
        return ResponseEntity.ok(eventClosingResponseDto);
    }
}

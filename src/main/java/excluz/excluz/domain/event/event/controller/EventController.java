package excluz.excluz.domain.event.event.controller;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.common.entity.Event;
import excluz.excluz.domain.event.event.dto.EventClosingResponseDto;
import excluz.excluz.domain.event.event.dto.EventRequestDto;
import excluz.excluz.domain.event.event.dto.EventResponseDto;
import excluz.excluz.domain.event.event.service.EventService;
import excluz.excluz.domain.user.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 스트리머 로직
    @PostMapping()
    @PreAuthorize("hasRole('STREAMER')")
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventRequestDto eventRequestDto) {
        Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
        EventResponseDto eventResponseDto = eventService.createEvent(streamerId, eventRequestDto);
        return ResponseEntity.status(201).body(eventResponseDto);
    }

    @PatchMapping("/{eventId}/applicants")
    @PreAuthorize("hasRole('STREAMER')")
    public ResponseEntity<EventClosingResponseDto> closeEvent(@PathVariable Integer eventId) {

        Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
        EventClosingResponseDto eventClosingResponseDto = eventService.closeEvent(streamerId, eventId);
        return ResponseEntity.ok(eventClosingResponseDto);
    }

//    공개된 조회 로직
    @GetMapping()
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        List<EventResponseDto> eventResponseDtoList = eventService.getAllEvents();
        return ResponseEntity.ok(eventResponseDtoList);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEvent(@PathVariable Integer eventId) {
        EventResponseDto eventResponseDto = eventService.getEvent(eventId);
        return ResponseEntity.ok(eventResponseDto);
    }

}

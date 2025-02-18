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

    @PostMapping()
    @PreAuthorize("hasRole('STREAMER')")
    public ResponseEntity<EventResponseDto> createEvent(@AuthenticationPrincipal User user,
                                                        @Valid @RequestBody EventRequestDto eventRequestDto) {
        Integer streamerId = Integer.parseInt(user.getUsername());
        EventResponseDto eventResponseDto = eventService.createEvent(streamerId, eventRequestDto);
        return ResponseEntity.status(201).body(eventResponseDto);
    }

    @GetMapping()
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
//        todo: 유저 인증 불필요
        List<EventResponseDto> eventResponseDtoList = eventService.getAllEvents();
        return ResponseEntity.ok(eventResponseDtoList);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEvent(@PathVariable Integer eventId) {
        // todo : 유저 인증 불필요
        EventResponseDto eventResponseDto = eventService.getEvent(eventId);
        return ResponseEntity.ok(eventResponseDto);
    }

    @DeleteMapping("/soft/{eventId}")
    @PreAuthorize("hasRole('STREAMER')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer eventId) {
        Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
        eventService.cancelEvent(streamerId, eventId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{eventId}/applicants")
    @PreAuthorize("hasRole('STREAMER')")
    public ResponseEntity<EventClosingResponseDto> closeEvent(@AuthenticationPrincipal User user,
                                                              @PathVariable Integer eventId) {

        Integer streamerId = Integer.parseInt(user.getUsername());
        EventClosingResponseDto eventClosingResponseDto = eventService.closeEvent(streamerId, eventId);
        return ResponseEntity.ok(eventClosingResponseDto);
    }
}

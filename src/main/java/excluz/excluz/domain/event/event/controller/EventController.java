package excluz.excluz.domain.event.event.controller;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.event.event.dto.EventClosingResponseDto;
import excluz.excluz.domain.event.event.dto.EventRequestDto;
import excluz.excluz.domain.event.event.dto.EventResponseDto;
import excluz.excluz.domain.event.event.dto.EventWithApplicantListResponseDto;
import excluz.excluz.domain.event.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


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

    @DeleteMapping("/{eventId}/soft")
    @PreAuthorize("hasRole('STREAMER')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer eventId) {
        Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
        eventService.cancelEvent(streamerId, eventId);
        return ResponseEntity.ok().build();
    }


    @GetMapping()
    @PreAuthorize("hasRole('STREAMER')")
    public ResponseEntity<Page<EventResponseDto>> getAllEvents(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
        Page<EventResponseDto> eventResponseDtoList = eventService.getEventList(streamerId, page, size);
        return ResponseEntity.ok(eventResponseDtoList);
    }

    @GetMapping("/{eventId}")
    @PreAuthorize("hasRole('STREAMER')")
    public ResponseEntity<EventWithApplicantListResponseDto> getEvent(@PathVariable Integer eventId) {
        Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
        EventWithApplicantListResponseDto eventResponseDto = eventService.getEvent(streamerId, eventId);
        return ResponseEntity.ok(eventResponseDto);
    }


}

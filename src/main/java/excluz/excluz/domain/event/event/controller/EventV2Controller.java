package excluz.excluz.domain.event.event.controller;


import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.event.event.dto.EventResponseWithoutEventItemDto;
import excluz.excluz.domain.event.event.dto.EventWithApplicantListResponseDto;
import excluz.excluz.domain.event.event.service.EventService;
import excluz.excluz.domain.event.event.service.EventV2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/events")
@RequiredArgsConstructor
public class EventV2Controller {
    private final EventV2Service eventV2Service;

    @GetMapping()
    @PreAuthorize("hasRole('STREAMER')")
    public ResponseEntity<Page<EventResponseWithoutEventItemDto>> getAllEvents(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size) {
        Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
        Page<EventResponseWithoutEventItemDto> eventResponseDtoList = eventV2Service.getEventList(streamerId, page, size);
        return ResponseEntity.ok(eventResponseDtoList);
    }

}

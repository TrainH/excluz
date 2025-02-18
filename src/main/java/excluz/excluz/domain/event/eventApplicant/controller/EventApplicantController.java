package excluz.excluz.domain.event.eventApplicant.controller;

import excluz.excluz.domain.event.event.dto.EventResponseDto;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantReadRequestDto;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantRequestDto;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantResponseDto;
import excluz.excluz.domain.event.eventApplicant.service.EventApplicantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events/applicants")
@Slf4j
@RequiredArgsConstructor
public class EventApplicantController {

    private final EventApplicantService eventApplicantService;

    @PostMapping
    public EventApplicantResponseDto applyForEvent(@RequestParam("code") String code,
                                                   @Valid  @RequestBody EventApplicantRequestDto requestDto) {
        return eventApplicantService.applyForEvent(code, requestDto);
    }

    @GetMapping
    public EventApplicantResponseDto getEventApplication(@RequestParam("code") String code,
                                                         @Valid @RequestBody EventApplicantReadRequestDto requestDto) {
        return eventApplicantService.getEventApplication(code, requestDto.getEmail(), requestDto.getApplicantPassword());
    }

    @DeleteMapping("/{eventApplicantId}")
    public ResponseEntity<Void> deleteEventApplicant(@RequestParam("code") String code,
                                                     @Valid @RequestBody EventApplicantReadRequestDto requestDto) {
        eventApplicantService.cancelEventApplicant(code, requestDto.getEmail(), requestDto.getApplicantPassword());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{eventApplicantId}")
    public ResponseEntity<EventApplicantResponseDto> confirmReceipt(
            @PathVariable Integer eventApplicantId,
            @RequestBody EventApplicantRequestDto requestDto) {
        // todo: 필요한 경우 비회원 인증 여부를 확인할 수도 있음 (예: 비밀번호 체크 등)
        EventApplicantResponseDto updated = eventApplicantService.confirmReceipt(eventApplicantId, requestDto);
        return ResponseEntity.ok(updated);
    }



}
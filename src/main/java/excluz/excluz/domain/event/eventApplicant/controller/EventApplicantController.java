package excluz.excluz.domain.event.eventApplicant.controller;

import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantReadRequestDto;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantRequestDto;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantResponseDto;
import excluz.excluz.domain.event.eventApplicant.service.EventApplicantService;
import excluz.excluz.domain.kakao.kakao.service.KakaoMessageService;
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
    private final KakaoMessageService kakaoMessageService;

    @PostMapping
    public EventApplicantResponseDto applyForEvent(
            @RequestParam("code") String code,
            @RequestHeader(value = "Kakao-Authorization", required = false) String kakaoAuthorizationHeader,
            @Valid @RequestBody EventApplicantRequestDto requestDto
    ) {
        // 1) 이벤트에 응모
        EventApplicantResponseDto responseDto = eventApplicantService.applyForEvent(code, requestDto);

        // 2) 카카오 메시지 전송 로직은 “별도 서비스”로 분리
        //    사용자가 Kakao-Authorization 헤더를 보냈다면 메시지를 전송
        if (kakaoAuthorizationHeader != null && !kakaoAuthorizationHeader.isEmpty()) {
            responseDto.updateEventCode(code);
            kakaoMessageService.sendApplicationResultMessage(kakaoAuthorizationHeader, responseDto);
        }

        return responseDto;
    }

    @PostMapping("/myinfo")
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
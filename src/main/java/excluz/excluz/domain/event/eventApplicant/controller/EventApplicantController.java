package excluz.excluz.domain.event.eventApplicant.controller;

import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantRequestDto;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantResponseDto;
import excluz.excluz.domain.event.eventApplicant.service.EventApplicantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events/applicants")
@Slf4j
@RequiredArgsConstructor
public class EventApplicantController {

    private final EventApplicantService eventApplicantService;

    @PostMapping
    public EventApplicantResponseDto applyForEvent(@RequestParam("code") String code,
                                                   @RequestBody EventApplicantRequestDto requestDto) {
        return eventApplicantService.applyForEvent(code, requestDto);
    }

    @GetMapping
    public EventApplicantResponseDto getEventApplication(@RequestParam("code") String code,
                                                         @RequestBody EventApplicantRequestDto requestDto) {
        return eventApplicantService.getEventApplication(code, requestDto.getEmail(), requestDto.getApplicantPassword());
    }
}
package excluz.excluz.domain.event.eventApplicant.service;


import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.EventApplicant;
import excluz.excluz.domain.event.event.repository.EventRepository;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantRequestDto;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantResponseDto;
import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import excluz.excluz.domain.event.eventApplicant.repository.EventApplicantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventApplicantService {

    private final EventApplicantRepository eventApplicantRepository;
    private final EventRepository eventRepository;

    public EventApplicantResponseDto applyForEvent(String code, EventApplicantRequestDto requestDto) {
        Event event = eventRepository.findByGeneratedCode(code)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 이벤트 코드입니다."));

        EventApplicant eventApplicant = EventApplicant.builder()
                .event(event)
                .applicantName(requestDto.getApplicantName())
                .email(requestDto.getEmail())
                .applicantPassword(requestDto.getApplicantPassword())
                .deliveryAddress(requestDto.getDeliveryAddress())
                .applicantStatus(ApplicantStatus.WAITING) // 초기 상태는 WAITING
                .build();

        EventApplicant savedApplicant = eventApplicantRepository.save(eventApplicant);

        return EventApplicantResponseDto.from(savedApplicant);
    }

    public EventApplicantResponseDto getEventApplication(String code, String email, String applicantPassword) {
        Event event = eventRepository.findByGeneratedCode(code)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 이벤트 코드입니다."));

        EventApplicant eventApplicant = eventApplicantRepository.findByEventAndEmailAndApplicantPassword(event, email, applicantPassword)
                .orElseThrow(() -> new IllegalArgumentException("응모 정보를 찾을 수 없거나 잘못된 인증 정보입니다."));

        return EventApplicantResponseDto.from(eventApplicant);
    }

    public EventApplicantResponseDto confirmReceipt(Integer eventApplicantId, EventApplicantRequestDto requestDto) {
        EventApplicant eventApplicant = eventApplicantRepository.findById(eventApplicantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 응모 정보를 찾을 수 없습니다."));

        if (eventApplicant.getApplicantStatus() != ApplicantStatus.WINNER) {
            throw new IllegalArgumentException("당첨(WINNER) 상태가 아닌 유저의 수령 확정은 불가능합니다.");
        }

        if (requestDto.getApplicantName() != null) {
            eventApplicant.setApplicantName(requestDto.getApplicantName());
        }
        if (requestDto.getDeliveryAddress() != null) {
            eventApplicant.updateDeliveryAddress(requestDto.getDeliveryAddress());
        }

        eventApplicant.updateApplicantStatus(ApplicantStatus.CONFIRMED);

        EventApplicant updatedApplicant = eventApplicantRepository.save(eventApplicant);

        return EventApplicantResponseDto.from(updatedApplicant);
    }
}
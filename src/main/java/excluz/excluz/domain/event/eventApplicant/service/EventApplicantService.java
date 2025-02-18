package excluz.excluz.domain.event.eventApplicant.service;


import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.EventApplicant;
import excluz.excluz.domain.event.event.enums.SelectionMethod;
import excluz.excluz.domain.event.event.repository.EventRepository;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantRequestDto;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantResponseDto;
import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import excluz.excluz.domain.event.eventApplicant.repository.EventApplicantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventApplicantService {

    private final EventApplicantRepository eventApplicantRepository;
    private final EventRepository eventRepository;

    public EventApplicantResponseDto applyForEvent(String code, EventApplicantRequestDto requestDto) {
        Event event = eventRepository.findByGeneratedCode(code)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 이벤트 코드입니다."));

        if (event.getIsCompleted()) {
            throw new IllegalArgumentException("이미 마감된 이벤트입니다.");
        }
        if (event.getIsDeleted()) {
            throw new IllegalArgumentException("취소된 이벤트입니다..");
        }

//        이벤트 시간 관련 로직
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(event.getStartDatetime())) {
            throw new IllegalStateException("아직 이벤트가 시작되지 않았습니다.");
        }
        if (now.isAfter(event.getEndDatetime())) {
            throw new IllegalStateException("이벤트가 이미 종료되었습니다.");
        }

        if (eventApplicantRepository.existsByEventAndEmail(event, requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 해당 이벤트에 응모한 이메일입니다.");
        }

        EventApplicant eventApplicant = EventApplicant.builder()
                .event(event)
                .applicantName(requestDto.getApplicantName())
                .email(requestDto.getEmail())
                .applicantPassword(requestDto.getApplicantPassword())
                .deliveryAddress(requestDto.getDeliveryAddress())
                .build();

        if (event.getSelectionMethod() == SelectionMethod.FIRST_COME_FIRST_SERVED) {
            int numberOfCurrentWinners = eventApplicantRepository.countByEventAndApplicantStatus(event, ApplicantStatus.WINNER);
            if (numberOfCurrentWinners < event.getNumberOfWinners()) {
                eventApplicant.updateApplicantStatus(ApplicantStatus.WINNER);
            } else {
                eventApplicant.updateApplicantStatus(ApplicantStatus.LOSER);
            }
        } else {
            eventApplicant.updateApplicantStatus(ApplicantStatus.WAITING);
        }

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

    @Transactional
    public void cancelEventApplicant(String code, String email, String applicantPassword) {
        Event event = eventRepository.findByGeneratedCode(code)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 이벤트 코드입니다."));

        EventApplicant eventApplicant = eventApplicantRepository.findByEventAndEmailAndApplicantPassword(event, email, applicantPassword)
                .orElseThrow(() -> new IllegalArgumentException("응모 정보를 찾을 수 없거나 잘못된 인증 정보입니다."));

        if (eventApplicant.getApplicantStatus().equals(ApplicantStatus.CONFIRMED)){
            throw new IllegalArgumentException("이미 수령 확정한 응모를 취소할 수 없습니다.");
        }

        eventApplicantRepository.delete(eventApplicant);
    }

    public EventApplicantResponseDto confirmReceipt(Integer eventApplicantId, EventApplicantRequestDto requestDto) {
        EventApplicant eventApplicant = eventApplicantRepository.findById(eventApplicantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 응모 정보를 찾을 수 없습니다."));

        if (eventApplicant.getApplicantStatus() != ApplicantStatus.WINNER) {
            throw new IllegalArgumentException("당첨(WINNER) 상태가 아닌 유저의 수령 확정은 불가능합니다.");
        }

        if (requestDto.getApplicantName() != null) {
            eventApplicant.updateApplicantName(requestDto.getApplicantName());
        }
        if (requestDto.getDeliveryAddress() != null) {
            eventApplicant.updateDeliveryAddress(requestDto.getDeliveryAddress());
        }

        eventApplicant.updateApplicantStatus(ApplicantStatus.CONFIRMED);
        EventApplicant updatedApplicant = eventApplicantRepository.save(eventApplicant);
        return EventApplicantResponseDto.from(updatedApplicant);
    }
}
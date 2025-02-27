package excluz.excluz.domain.event.eventApplicant.service;


import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.EventApplicant;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.event.event.enums.SelectionMethod;
import excluz.excluz.domain.event.event.repository.EventRepository;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantRequestDto;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantResponseDto;
import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import excluz.excluz.domain.event.eventApplicant.repository.EventApplicantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Slf4j
@RequiredArgsConstructor
public class EventApplicantService {

    private final EventApplicantRepository eventApplicantRepository;
    private final EventRepository eventRepository;

    @Transactional(propagation=Propagation.REQUIRED) // todo: 락 테스트
    public EventApplicantResponseDto applyForEvent(String code, EventApplicantRequestDto requestDto) {
        Event event = eventRepository.findByGeneratedCode(code)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_NOT_FOUND));

        if (event.getIsCompleted()) {
            throw new BadRequestException(ErrorCode.EVENT_ALREADY_CLOSED);
        }
        if (event.getIsDeleted()) {
            throw new BadRequestException(ErrorCode.EVENT_ALREADY_CANCELED);
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(event.getStartDatetime())) {
            throw new BadRequestException(ErrorCode.EVENT_APPLICANT_NOT_STARTED);
        }
        if (now.isAfter(event.getEndDatetime())) {
            throw new BadRequestException(ErrorCode.EVENT_APPLICANT_EXPIRED);
        }

        if (eventApplicantRepository.existsByEventAndEmail(event, requestDto.getEmail())) {
            throw new BadRequestException((ErrorCode.EMAIL_ALREADY_EXISTS));
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

    @Transactional
    public EventApplicantResponseDto getEventApplication(String code, String email, String applicantPassword) {
        Event event = eventRepository.findByGeneratedCode(code)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_NOT_FOUND));

        EventApplicant eventApplicant = eventApplicantRepository.findByEventAndEmailAndApplicantPassword(event, email, applicantPassword)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_APPLICANT_NOT_FOUND));

        return EventApplicantResponseDto.from(eventApplicant);
    }

    @Transactional
    public void cancelEventApplicant(String code, String email, String applicantPassword) {
        Event event = eventRepository.findByGeneratedCode(code)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_NOT_FOUND));

        EventApplicant eventApplicant = eventApplicantRepository.findByEventAndEmailAndApplicantPassword(event, email, applicantPassword)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_APPLICANT_NOT_FOUND));

        if (eventApplicant.getApplicantStatus().equals(ApplicantStatus.CONFIRMED)){
            throw new BadRequestException(ErrorCode.EVENT_APPLICANT_ALREADY_CONFIRMED);
        }

        if (event.getIsCompleted()) {
            throw new BadRequestException(ErrorCode.EVENT_ALREADY_CLOSED);
        }

        eventApplicantRepository.delete(eventApplicant);
    }

    public EventApplicantResponseDto confirmReceipt(Integer eventApplicantId, EventApplicantRequestDto requestDto) {
        EventApplicant eventApplicant = eventApplicantRepository.findById(eventApplicantId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EVENT_APPLICANT_NOT_FOUND));

        if (eventApplicant.getApplicantStatus() != ApplicantStatus.WINNER) {
            throw new BadRequestException(ErrorCode.EVENT_APPLICANT_NOT_WINNER);
        }

        if (eventApplicant.getEvent().getIsDeleted()) {
            throw new BadRequestException(ErrorCode.EVENT_ALREADY_CANCELED);
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
package excluz.excluz.domain.event.eventApplicant.dto;

import excluz.excluz.common.entity.EventApplicant;
import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EventApplicantReviewResponseDto {
    private Integer id;
    private String email;
    private String applicantName;
    private String deliveryAddress;
    private ApplicantStatus applicantStatus;

    @Builder
    public EventApplicantReviewResponseDto(Integer id, String email, String applicantName, String deliveryAddress, ApplicantStatus applicantStatus) {
        this.id = id;
        this.email = email;
        this.applicantName = applicantName;
        this.deliveryAddress = deliveryAddress;
        this.applicantStatus = applicantStatus;
    }

    public static EventApplicantReviewResponseDto from(EventApplicant applicant) {
        return EventApplicantReviewResponseDto.builder()
                .id(applicant.getId())
                .email(applicant.getEmail())
                .applicantName(applicant.getApplicantName())
                .deliveryAddress(applicant.getDeliveryAddress())
                .applicantStatus(applicant.getApplicantStatus())
                .build();
    }
}
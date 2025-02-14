package excluz.excluz.domain.event.eventApplicant.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EventApplicantRequestDto {
    private String email;
    private String applicantPassword;
    private String applicantName;
    private String deliveryAddress;

    @Builder
    public EventApplicantRequestDto(String email, String applicantName, String applicantPassword, String deliveryAddress) {
        this.email = email;
        this.applicantName = applicantName;
        this.applicantPassword = applicantPassword;
        this.deliveryAddress = deliveryAddress;
    }
}
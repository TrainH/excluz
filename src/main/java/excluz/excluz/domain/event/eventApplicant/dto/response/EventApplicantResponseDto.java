package excluz.excluz.domain.event.eventApplicant.dto.response;

import excluz.excluz.common.entity.EventApplicant;
import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EventApplicantResponseDto {
    private Integer id;
    private Integer eventId;
    private String email;
    private String applicantName;
    private String deliveryAddress;
    private ApplicantStatus applicantStatus;
    private String eventCodeForKakaoLink;


    @Builder
    public EventApplicantResponseDto(Integer id, Integer eventId, String email, String applicantName, ApplicantStatus applicantStatus, String deliveryAddress, String eventCode) {
        this.id = id;
        this.eventId = eventId;
        this.email = email;
        this.applicantName = applicantName;
        this.deliveryAddress = deliveryAddress;
        this.applicantStatus = applicantStatus;
        this.eventCodeForKakaoLink = eventCode;
    }

    public static EventApplicantResponseDto from(EventApplicant eventApplicant) {
        return EventApplicantResponseDto.builder()
                .id(eventApplicant.getId())
                .eventId(eventApplicant.getEvent().getId())
                .email(eventApplicant.getEmail())
                .applicantName(eventApplicant.getApplicantName())
                .deliveryAddress(eventApplicant.getDeliveryAddress())
                .applicantStatus(eventApplicant.getApplicantStatus())
                .build();
    }

    public void updateEventCode(String eventCode){
        this.eventCodeForKakaoLink = eventCode;
    }
}

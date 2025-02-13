package excluz.excluz.common.entity;


import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "event_applicants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventApplicant extends BaseEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "email", length = 50, unique = true, nullable = false)
    private String email;

    @Column(name = "applicant_name", length = 10, nullable = false)
    private String applicantName;

    @Column(name = "applicant_password", length = 30, nullable = false)
    private String applicantPassword;

    @Column(name = "delivery_address", length = 100, nullable = false)
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "applicant_status", nullable = false)
    private ApplicantStatus applicantStatus;


    // 생성자: 매개변수 4개 이상이므로 @Builder 패턴 사용
    @Builder
    public EventApplicant(Event event,
                          String applicantName,
                          String email,
                          String applicantPassword,
                          String deliveryAddress,
                          ApplicantStatus applicantStatus) {
        this.event = event;
        this.applicantName = applicantName;
        this.email = email;
        this.applicantPassword = applicantPassword;
        this.deliveryAddress = deliveryAddress;
        this.applicantStatus = applicantStatus;
    }

    // Setter 대신 필요한 필드에 대한 개별 메서드 생성
    public void updateApplicantStatus(ApplicantStatus applicantStatus) {
        this.applicantStatus = applicantStatus;
    }

    public void updateDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    // 필요에 따라 비밀번호를 변경하는 메서드 생성 (보안 고려)
    public void changePassword(String newPassword) {
        this.applicantPassword = newPassword;
    }
}
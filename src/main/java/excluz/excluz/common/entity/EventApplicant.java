package excluz.excluz.common.entity;


import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "event_applicants")
@NoArgsConstructor
public class EventApplicant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "applicant_name", length = 10)
    private String applicantName;

    @Column(name = "email", length = 50, unique = true)
    private String email;

    @Column(name = "applicant_password", length = 30)
    private String applicantPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "applicant_status")
    private ApplicantStatus applicantStatus;

    @Column(name = "delivery_address", length = 100)
    private String deliveryAddress;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 생성자: 매개변수 4개 이상이므로 @Builder 패턴 사용
    @Builder
    public EventApplicant(Event event,
                          String applicantName,
                          String email,
                          String applicantPassword,
                          ApplicantStatus applicantStatus,
                          String deliveryAddress) {
        this.event = event;
        this.applicantName = applicantName;
        this.email = email;
        this.applicantPassword = applicantPassword;
        this.applicantStatus = applicantStatus;
        this.deliveryAddress = deliveryAddress;
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
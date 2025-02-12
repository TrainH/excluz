package excluz.excluz.common.entity;

import excluz.excluz.domain.event.event.enums.ParticipantCondition;
import excluz.excluz.domain.event.event.enums.SelectionMethod;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "events")
@NoArgsConstructor
public class Event extends BaseEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // StreamerStore 엔티티와의 Many-to-One 관계 매핑 (_id로 끝나는 컬럼 처리)
    @ManyToOne
    @JoinColumn(name = "streamer_store_id") // 컬럼 이름은 snake_case
    private Store store;

    @Column(name = "number_of_winners")
    private Integer numberOfWinners;

    @Column(name = "generated_code", length = 30, unique = true)
    private String generatedCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_condition")
    private ParticipantCondition participantCondition;

    @Enumerated(EnumType.STRING)
    @Column(name = "selection_method")
    private SelectionMethod selectionMethod;

    @Column(name = "start_datetime")
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;

    @Column(name = "is_completed")
    private Boolean isCompleted;


    // 생성자: 매개변수 4개 이상이므로 @Builder 패턴 사용
    @Builder
    public Event(Store store,
                 Integer numberOfWinners,
                 String generatedCode,
                 ParticipantCondition participantCondition,
                 SelectionMethod selectionMethod,
                 LocalDateTime startDatetime,
                 LocalDateTime endDatetime,
                 Boolean isCompleted) {
        this.store = store;
        this.numberOfWinners = numberOfWinners;
        this.generatedCode = generatedCode;
        this.participantCondition = participantCondition;
        this.selectionMethod = selectionMethod;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.isCompleted = isCompleted;
    }

    public void updateNumberOfWinners(Integer numberOfWinners) {
        this.numberOfWinners = numberOfWinners;
    }

    public void updateStartDatetime(LocalDateTime startDatetime) {
        this.startDatetime = startDatetime;
    }

    public void updateEndDatetime(LocalDateTime endDatetime) {
        this.endDatetime = endDatetime;
    }

    public void completeEvent() {
        this.isCompleted = true;
    }
}
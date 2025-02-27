package excluz.excluz.common.entity;

import excluz.excluz.domain.event.event.enums.ParticipantCondition;
import excluz.excluz.domain.event.event.enums.SelectionMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_events_generated_code", columnList = "generated_code"),
        @Index(name = "idx_events_start_datetime", columnList = "start_datetime"),
        @Index(name = "idx_events_streamer_store_id", columnList = "streamer_store_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "streamer_store_id",  nullable = false)
    private Store store;

    @Column(name = "number_of_winners", nullable = false)
    private Integer numberOfWinners;

    @Column(name = "generated_code", length = 30, unique = true,  nullable = false)
    private String generatedCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_condition", nullable = false)
    private ParticipantCondition participantCondition;

    @Enumerated(EnumType.STRING)
    @Column(name = "selection_method", nullable = false)
    private SelectionMethod selectionMethod;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime",  nullable = false)
    private LocalDateTime endDatetime;

    @Column(name = "is_completed",  nullable = false)
    private Boolean isCompleted;

    @Column(name = "is_deleted",  nullable = false)
    private Boolean isDeleted;

    @Builder
    public Event(Store store,
                 Integer numberOfWinners,
                 String generatedCode,
                 ParticipantCondition participantCondition,
                 SelectionMethod selectionMethod,
                 LocalDateTime startDatetime,
                 LocalDateTime endDatetime) {
        this.store = store;
        this.numberOfWinners = numberOfWinners;
        this.generatedCode = generatedCode;
        this.participantCondition = participantCondition;
        this.selectionMethod = selectionMethod;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.isCompleted = false;
        this.isDeleted = false;
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

    public void updateIsDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
    }

    public void completeEvent() {
        this.isCompleted = true;
    }
}
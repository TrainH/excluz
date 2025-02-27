package excluz.excluz.domain.event.event.dto;

import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.EventItem;
import excluz.excluz.domain.event.event.enums.ParticipantCondition;
import excluz.excluz.domain.event.event.enums.SelectionMethod;
import excluz.excluz.domain.event.eventItem.dto.EventItemDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class EventResponseWithoutEventItemDto {
    private Integer id;
    private Integer streamerStoreId;
    private Integer numberOfWinners;
    private ParticipantCondition participantCondition;
    private SelectionMethod selectionMethod;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Boolean isCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String generatedCode;

    @Builder
    public EventResponseWithoutEventItemDto(Integer id,
                                            Integer streamerStoreId,
                                            Integer numberOfWinners,
                                            ParticipantCondition participantCondition,
                                            SelectionMethod selectionMethod,
                                            LocalDateTime startDatetime,
                                            LocalDateTime endDatetime,
                                            Boolean isCompleted,
                                            LocalDateTime createdAt,
                                            LocalDateTime updatedAt,
                                            String generatedCode) {
        this.id = id;
        this.streamerStoreId = streamerStoreId;
        this.numberOfWinners = numberOfWinners;
        this.participantCondition = participantCondition;
        this.selectionMethod = selectionMethod;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.generatedCode = generatedCode;
    }


    public static EventResponseWithoutEventItemDto from(Event event) {

        return EventResponseWithoutEventItemDto.builder()
                .id(event.getId())
                .streamerStoreId(event.getStore().getId())
                .numberOfWinners(event.getNumberOfWinners())
                .participantCondition(event.getParticipantCondition())
                .selectionMethod(event.getSelectionMethod())
                .startDatetime(event.getStartDatetime())
                .endDatetime(event.getEndDatetime())
                .isCompleted(event.getIsCompleted())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .generatedCode(event.getGeneratedCode())
                .build();
    }


}
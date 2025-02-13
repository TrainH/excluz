package excluz.excluz.domain.event.event.dto;

import excluz.excluz.domain.event.eventItem.dto.EventItemRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class EventRequestDto {
    private Integer streamerStoreId;
    private Integer numberOfWinners;
    private String participantCondition;
    private String selectionMethod;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private List<EventItemRequestDto> eventItems;

    @Builder
    public EventRequestDto(Integer streamerStoreId,
                           Integer numberOfWinners,
                           String participantCondition,
                           String selectionMethod,
                           LocalDateTime startDatetime,
                           LocalDateTime endDatetime,
                           List<EventItemRequestDto> eventItems) {
        this.streamerStoreId = streamerStoreId;
        this.numberOfWinners = numberOfWinners;
        this.participantCondition = participantCondition;
        this.selectionMethod = selectionMethod;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.eventItems = eventItems;
    }
}
package excluz.excluz.domain.event.event.dto;

import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.EventItem;
import excluz.excluz.domain.event.eventItem.dto.EventItemDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class EventResponseDto {
    private Integer id;
    private Integer streamerStoreId;
    private Integer numberOfWinners;
    private String participantCondition;
    private String selectionMethod;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Boolean isCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String generatedCode;
    private List<EventItemDto> eventItems;

    @Builder
    public EventResponseDto(Integer id,
                            Integer streamerStoreId,
                            Integer numberOfWinners,
                            String participantCondition,
                            String selectionMethod,
                            LocalDateTime startDatetime,
                            LocalDateTime endDatetime,
                            Boolean isCompleted,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt,
                            String generatedCode,
                            List<EventItemDto> eventItems) {
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
        this.eventItems = eventItems;
    }

    // Event 엔티티와 EventItem 리스트를 받아서 EventResponseDto로 변환하는 정적 메서드
    public static EventResponseDto from(Event event, List<EventItem> eventItems) {
        List<EventItemDto> eventItemDtos = null;
        if (eventItems != null) {
            eventItemDtos = eventItems.stream()
                    .map(item -> EventItemDto.builder()
                            .id(item.getId())
                            .quantity(item.getQuantity())
                            // 필요한 다른 필드들을 추가
                            .build())
                    .collect(Collectors.toList());
        }

        return EventResponseDto.builder()
                .id(event.getId())
                .streamerStoreId(event.getStore().getId())
                .numberOfWinners(event.getNumberOfWinners())
                .participantCondition(event.getParticipantCondition().name())
                .selectionMethod(event.getSelectionMethod().name())
                .startDatetime(event.getStartDatetime())
                .endDatetime(event.getEndDatetime())
                .isCompleted(event.getIsCompleted())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .generatedCode(event.getGeneratedCode())
                .eventItems(eventItemDtos)
                .build();
    }

    // EventItems 없이 EventResponseDto를 생성하는 정적 메서드
    public static EventResponseDto fromWithoutItems(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .streamerStoreId(event.getStore().getId())
                .numberOfWinners(event.getNumberOfWinners())
                .participantCondition(event.getParticipantCondition().name())
                .selectionMethod(event.getSelectionMethod().name())
                .startDatetime(event.getStartDatetime())
                .endDatetime(event.getEndDatetime())
                .isCompleted(event.getIsCompleted())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .generatedCode(event.getGeneratedCode())
                .eventItems(null) // EventItems를 포함하지 않음
                .build();
    }
}
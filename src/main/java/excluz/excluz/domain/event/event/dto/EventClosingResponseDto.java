package excluz.excluz.domain.event.event.dto;

import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.EventApplicant;
import excluz.excluz.common.entity.EventItem;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantResponseDto;
import excluz.excluz.domain.event.eventItem.dto.EventItemDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class EventClosingResponseDto {
    private Integer id;
    private Integer storeId;
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
    private List<EventApplicantResponseDto> eventApplicants; // 응모자 정보 추가

    @Builder
    public EventClosingResponseDto(Integer id,
                                   Integer storeId,
                                   Integer numberOfWinners,
                                   String generatedCode,
                                   String participantCondition,
                                   String selectionMethod,
                                   LocalDateTime startDatetime,
                                   LocalDateTime endDatetime,
                                   Boolean isCompleted,
                                   List<EventItemDto> eventItems,
                                   List<EventApplicantResponseDto> eventApplicants) {
        this.id = id;
        this.storeId = storeId;
        this.numberOfWinners = numberOfWinners;
        this.generatedCode = generatedCode;
        this.participantCondition = participantCondition;
        this.selectionMethod = selectionMethod;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.isCompleted = isCompleted;
        this.eventItems = eventItems;
        this.eventApplicants = eventApplicants;
    }

    // 응모자 정보를 포함하여 응답 DTO 생성하는 메서드
    public static EventClosingResponseDto from(Event event, List<EventItem> eventItems, List<EventApplicant> eventApplicants) {
        List<EventItemDto> eventItemDtos = null;
        if (eventItems != null) {
            eventItemDtos = eventItems.stream()
                    .map(EventItemDto::from)
                    .collect(Collectors.toList());
        }

        List<EventApplicantResponseDto> applicantDtos = null;
        if (eventApplicants != null) {
            applicantDtos = eventApplicants.stream()
                    .map(EventApplicantResponseDto::from)
                    .collect(Collectors.toList());
        }

        return EventClosingResponseDto.builder()
                .id(event.getId())
                .storeId(event.getStore().getId())
                .numberOfWinners(event.getNumberOfWinners())
                .generatedCode(event.getGeneratedCode())
                .participantCondition(event.getParticipantCondition().name())
                .selectionMethod(event.getSelectionMethod().name())
                .startDatetime(event.getStartDatetime())
                .endDatetime(event.getEndDatetime())
                .isCompleted(event.getIsCompleted())
                .eventItems(eventItemDtos)
                .eventApplicants(applicantDtos)
                .build();
    }
}
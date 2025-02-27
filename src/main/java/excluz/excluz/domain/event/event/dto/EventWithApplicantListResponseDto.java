package excluz.excluz.domain.event.event.dto;

import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.EventApplicant;
import excluz.excluz.common.entity.EventItem;
import excluz.excluz.domain.event.event.enums.ParticipantCondition;
import excluz.excluz.domain.event.event.enums.SelectionMethod;
import excluz.excluz.domain.event.eventApplicant.dto.EventApplicantReviewResponseDto;
import excluz.excluz.domain.event.eventItem.dto.EventItemDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class EventWithApplicantListResponseDto {

    private Integer id;
    private String generatedCode;
    private ParticipantCondition participantCondition;
    private SelectionMethod selectionMethod;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Boolean isCompleted;
    private Boolean isDeleted;

    private List<EventItemDto> eventItemList; // 이벤트 아이템 목록 Dto
    private List<EventApplicantReviewResponseDto> eventApplicantList; // 이벤트 지원자 목록 Dto

    @Builder
    public EventWithApplicantListResponseDto(Integer id, String generatedCode, ParticipantCondition participantCondition,
                                             SelectionMethod selectionMethod,
                                             LocalDateTime startDatetime, LocalDateTime endDatetime, Boolean isCompleted, Boolean isDeleted,
                                             List<EventItemDto> eventItemList, List<EventApplicantReviewResponseDto> eventApplicantList) {
        this.id = id;
        this.generatedCode = generatedCode;
        this.participantCondition = participantCondition;
        this.selectionMethod = selectionMethod;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.isCompleted = isCompleted;
        this.isDeleted = isDeleted;

        this.eventItemList = eventItemList;
        this.eventApplicantList = eventApplicantList;

    }

    public static EventWithApplicantListResponseDto from(Event event,
                                                         List<EventItem> eventItemList,
                                                         List<EventApplicant> eventApplicantList) {

        List<EventItemDto> itemDtoList = eventItemList.stream()
                .map(EventItemDto::from)
                .collect(Collectors.toList());

        // 이벤트 지원자 목록을 담을 Dto 리스트 생성
        List<EventApplicantReviewResponseDto> applicantDtoList = eventApplicantList.stream()
                .map(EventApplicantReviewResponseDto::from)
                .toList();

        return EventWithApplicantListResponseDto.builder()
                .id(event.getId())
                .generatedCode(event.getGeneratedCode())
                .participantCondition(event.getParticipantCondition())
                .selectionMethod(event.getSelectionMethod())
                .startDatetime(event.getStartDatetime())
                .endDatetime(event.getEndDatetime())
                .isCompleted(event.getIsCompleted())
                .isDeleted(event.getIsDeleted())
                .eventItemList(itemDtoList)
                .eventApplicantList(applicantDtoList)
                .build();
    }


}
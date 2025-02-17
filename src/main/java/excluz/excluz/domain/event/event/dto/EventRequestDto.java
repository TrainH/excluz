package excluz.excluz.domain.event.event.dto;

import excluz.excluz.domain.event.eventItem.dto.EventItemRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class EventRequestDto {
    @NotNull(message = "스토어 ID는 필수입니다.")
    private Integer storeId;

    @NotNull(message = "당첨자 수는 필수입니다.")
    @Positive(message = "당첨자 수는 최소 1명 이상이어야 합니다.")
    private Integer numberOfWinners;

    @NotBlank(message = "참가 조건은 비어있을 수 없습니다.")
    private String participantCondition;

    @NotBlank(message = "당첨자 선별 방식은 비어있을 수 없습니다.")
    private String selectionMethod;

    @NotNull(message = "이벤트 시작일시는 필수입니다.")
    private LocalDateTime startDatetime;

    @NotNull(message = "이벤트 종료일시는 필수입니다.")
    private LocalDateTime endDatetime;

    @NotNull(message = "이벤트에 사용될 아이템이 지정되어야 합니다..")
    @NotEmpty
    @Valid
    private List<EventItemRequestDto> eventItemList;

    @AssertTrue(message = "이벤트 시작일시는 종료일시 이전이어야 합니다.")
    public boolean isValidDateRange() {
        // NotNull 검증은 위의 어노테이션이 처리하므로 null일 경우 검증 통과하도록 처리
        if (startDatetime == null || endDatetime == null) {
            return true;
        }
        return startDatetime.isBefore(endDatetime);
    }

    @Builder
    public EventRequestDto(Integer storeId,
                           Integer numberOfWinners,
                           String participantCondition,
                           String selectionMethod,
                           LocalDateTime startDatetime,
                           LocalDateTime endDatetime,
                           List<EventItemRequestDto> eventItemList) {
        this.storeId = storeId;
        this.numberOfWinners = numberOfWinners;
        this.participantCondition = participantCondition;
        this.selectionMethod = selectionMethod;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.eventItemList = eventItemList;
    }
}
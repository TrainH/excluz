package excluz.excluz.domain.event.eventItem.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EventItemRequestDto {
    @NotNull(message = "상품 ID는 필수입니다.")
    private Integer itemId;

    @NotNull(message = "상품 수량은 필수입니다.")
    @Positive(message = "상품 수량은 1개 이상이어야 합니다.")
    private Integer quantity;

    @Builder
    public EventItemRequestDto(Integer itemId, Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
package excluz.excluz.domain.event.eventItem.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EventItemRequestDto {
    private Integer itemId;
    private Integer quantity;


    public EventItemRequestDto(Integer itemId,
                               Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
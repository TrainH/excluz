package excluz.excluz.domain.event.eventItem.dto;

import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.EventItem;
import excluz.excluz.common.entity.Item;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EventItemDto {

    private Integer id;
    private Integer eventId;

    private Integer itemId;
    private Integer quantity;

    @Builder
    public EventItemDto(Integer id, Integer eventId, Integer itemId, Integer quantity) {
        this.id = id;
        this.eventId = eventId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public static EventItemDto from(EventItem eventItem) {
        return EventItemDto.builder()
                .id(eventItem.getId())
                .eventId(eventItem.getEvent().getId())
                .itemId(eventItem.getItem().getId())
                .quantity(eventItem.getQuantity())
                .build();
    }

}

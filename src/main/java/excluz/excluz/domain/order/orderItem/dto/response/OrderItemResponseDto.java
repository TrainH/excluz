package excluz.excluz.domain.order.orderItem.dto.response;

import excluz.excluz.common.entity.OrderItem;
import lombok.Builder;

@Builder
public record OrderItemResponseDto (
        String nickName,
        Integer itemId,
        String itemName,
        Integer price,
        Integer itemQuantity
) {
    public static OrderItemResponseDto from(OrderItem orderItem) {
        return new OrderItemResponseDto(
                orderItem.getItem().getStore().getStreamer().getNickName(),
                orderItem.getItem().getStore().getId(),
                orderItem.getItem().getItemName(),
                orderItem.getItem().getPrice(),
                orderItem.getItem_quantity()
        );
    }
}
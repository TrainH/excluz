package excluz.excluz.domain.order.orderItem.dto.response;

import excluz.excluz.common.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemResponseDto {

    private String nickName;
    private Integer itemId;
    private String itemName;
    private Integer price;
    private Integer itemQuantity;

    @Builder
    public OrderItemResponseDto(String nickName, Integer itemId, String itemName, Integer price, Integer itemQuantity) {
        this.nickName = nickName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.itemQuantity = itemQuantity;
    }

    public static OrderItemResponseDto from(OrderItem orderItem) {
        return OrderItemResponseDto.builder()
                .nickName(orderItem.getItem().getStore().getStreamer().getNickName())
                .itemId(orderItem.getItem().getId())
                .itemName(orderItem.getItem().getItemName())
                .price(orderItem.getItem().getPrice())
                .itemQuantity(orderItem.getItem_quantity())
                .build();
    }
}
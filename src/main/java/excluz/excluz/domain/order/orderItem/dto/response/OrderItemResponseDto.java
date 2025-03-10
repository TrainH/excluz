package excluz.excluz.domain.order.orderItem.dto.response;

import excluz.excluz.common.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class OrderItemResponseDto implements Serializable {

    private Integer orderId;
    private Integer orderItemId;
    private String nickName;
    private Integer itemId;
    private String itemName;
    private Integer price;
    private Integer itemQuantity;
    private LocalDateTime createdAt;

    @Builder
    public OrderItemResponseDto(Integer orderId, Integer orderItemId,
                                String nickName, Integer itemId,
                                String itemName, Integer price,
                                Integer itemQuantity, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.orderItemId = orderItemId;
        this.nickName = nickName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.itemQuantity = itemQuantity;
        this.createdAt = createdAt;
    }

    public static OrderItemResponseDto from(OrderItem orderItem) {
        return OrderItemResponseDto.builder()
                .orderId(orderItem.getOrder().getId())
                .orderItemId(orderItem.getId())
                .nickName(orderItem.getItem().getStore().getStreamer().getNickName())
                .itemId(orderItem.getItem().getId())
                .itemName(orderItem.getItem().getItemName())
                .price(orderItem.getItem().getPrice())
                .itemQuantity(orderItem.getItem_quantity())
                .createdAt(orderItem.getOrder().getCreatedAt())
                .build();
    }
}
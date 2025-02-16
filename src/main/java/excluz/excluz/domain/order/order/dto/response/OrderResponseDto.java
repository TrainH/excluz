package excluz.excluz.domain.order.order.dto.response;


import excluz.excluz.common.entity.Order;
import excluz.excluz.domain.order.order.enums.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class OrderResponseDto {
    private Integer orderId;
    private OrderStatus orderStatus;
    private String address;
    private LocalDateTime updatedAt;

    // Constructor
    public OrderResponseDto(Integer orderId, OrderStatus orderStatus, String address, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.address = address;
        this.updatedAt = updatedAt;
    }

    // from 메서드
    public static OrderResponseDto from(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getOrderStatus(),
                order.getAddress(),
                order.getUpdatedAt()
        );
    }
}

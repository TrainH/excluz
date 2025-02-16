package excluz.excluz.domain.order.order.dto.request;

import excluz.excluz.domain.order.order.enums.OrderStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderUpdateRequestDto {
    private OrderStatus orderStatus;
    private String address;

    public OrderUpdateRequestDto(OrderStatus orderStatus, String address) {
        this.orderStatus = orderStatus;
        this.address = address;
    }
}

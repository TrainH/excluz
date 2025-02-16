package excluz.excluz.common.entity;

import excluz.excluz.domain.order.order.dto.request.OrderUpdateRequestDto;
import excluz.excluz.domain.order.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "orders")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false, length = 100)
    private String address;

    public Order(User user, OrderStatus orderStatus, String address) {
        this.user = user;
        this.orderStatus = orderStatus;
        this.address = address;
    }

    public void updateWith(OrderUpdateRequestDto requestDto){
        this.orderStatus = requestDto.getOrderStatus();
        this.address = requestDto.getAddress();
    }
}

package excluz.excluz.common.entity;

import excluz.excluz.domain.order.order.enums.OrderStatus;
import jakarta.persistence.*;

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
}

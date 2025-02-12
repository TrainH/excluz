package excluz.excluz.common.entity;

import jakarta.persistence.*;

public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne( fetch = FetchType.LAZY, optional = false )
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne( fetch = FetchType.LAZY, optional = false )
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private int item_quantity;


    public OrderItem(Order order, Item item, Integer itemQuantity) {
        this.order = order;
        this.item = item;
        this.item_quantity = itemQuantity;
    }
}

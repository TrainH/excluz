package excluz.excluz.domain.order.orderItem.repository;

import excluz.excluz.common.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

}

package excluz.excluz.domain.order.order.repository;

import excluz.excluz.common.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}

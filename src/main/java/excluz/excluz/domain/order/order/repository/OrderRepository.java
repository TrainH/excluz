package excluz.excluz.domain.order.order.repository;

import excluz.excluz.common.entity.Order;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT new excluz.excluz.domain.order.order.dto.response.OrderResponseDto(" +
            "o.id, o.orderStatus, o.address, o.updatedAt) " +
            "FROM Order o " +
            "WHERE o.user.id = :userId")
    Page<OrderResponseDto> findByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT new excluz.excluz.domain.order.order.dto.response.OrderResponseDto(" +
            "o.id, o.orderStatus, o.address, o.updatedAt) " +
            "FROM Order o " +
            "LEFT JOIN OrderItem oi ON oi.order = o " +
            "LEFT JOIN oi.item i " +
            "LEFT JOIN i.store s " +
            "LEFT JOIN s.streamer st " +
            "WHERE st.id = :streamerId")
    Page<OrderResponseDto> findByStreamerId(@Param("streamerId") Integer streamerId, Pageable pageable);

    Optional<Order> findByIdAndUserId(Integer orderId, Integer userId);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN OrderItem oi ON oi.order = o " +
            "LEFT JOIN oi.item i " +
            "LEFT JOIN i.store s " +
            "LEFT JOIN s.streamer st " +
            "WHERE o.id = :orderId AND st.id = :streamerId")
    Optional<Order> findByIdAndStreamerId(@Param("orderId") Integer orderId, @Param("streamerId") Integer streamerId);

}

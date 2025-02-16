package excluz.excluz.domain.order.order.repository;

import excluz.excluz.common.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.user.id = :userId")
    Page<Order> findByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN o.user u " +
            "LEFT JOIN OrderItem oi ON oi.order = o " +
            "LEFT JOIN Item i ON oi.item = i " +
            "LEFT JOIN i.store s " +
            "LEFT JOIN s.streamer st " +
            "WHERE st.id = :streamerId")
    Page<Order> findByStreamerId(@Param("streamerId") Integer streamerId, Pageable pageable);

    Optional<Order> findByIdAndUserId(Integer orderId, Integer userId);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN o.user u " +
            "LEFT JOIN OrderItem oi ON oi.order = o " +
            "LEFT JOIN Item i ON oi.item = i " +
            "LEFT JOIN i.store s " +
            "LEFT JOIN s.streamer st " +
            "WHERE o.id = :orderId AND st.id = :streamerId")
    Optional<Order> findByIdAndStreamerId(@Param("orderId") Integer orderId,    @Param("streamerId") Integer streamerId);
}

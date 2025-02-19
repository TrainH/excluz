package excluz.excluz.domain.order.orderItem.repository;

import excluz.excluz.common.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    @Query("SELECT oi FROM OrderItem oi " +
            "JOIN FETCH oi.order o " +
            "JOIN FETCH o.user " +
            "WHERE o.user.id = :userId")
    Page<OrderItem> findByUserId(Integer userId, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi " +
            "JOIN FETCH oi.item i " +
            "JOIN FETCH i.store s " +
            "JOIN FETCH s.streamer st " +
            "WHERE st.id = :streamerId")
    Page<OrderItem> findByStreamerId(@Param("streamerId") Integer streamerId,  Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi " +
            "JOIN FETCH oi.order o " +
            "JOIN FETCH o.user " +
            "WHERE o.user.id = :userId AND oi.id =:orderItemId")
    Optional<OrderItem> getByIdAndUserId(@Param("orderItemId") Integer orderItemId,  @Param("userId") Integer userId);

    @Query("SELECT oi FROM OrderItem oi " +
            "JOIN FETCH oi.item i " +
            "JOIN FETCH i.store s " +
            "JOIN FETCH s.streamer st " +
            "WHERE st.id = :streamerId AND oi.id =:orderItemId")
    Optional<OrderItem> getByIdAndStreamerId(@Param("orderItemId") Integer orderItemId, @Param("streamerId") Integer streamerId);

}

package excluz.excluz.domain.order.orderItem.repository;

import excluz.excluz.common.entity.OrderItem;
import excluz.excluz.domain.order.orderItem.dto.response.OrderItemResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    @Query("SELECT new excluz.excluz.domain.order.orderItem.dto.response.OrderItemResponseDto( " +
            "o.id, oi.id, u.nickName, i.id, i.itemName, i.price, oi.item_quantity, o.createdAt) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN o.user u " +
            "JOIN oi.item i " +
            "JOIN i.store s " +
            "JOIN s.streamer st " +
            "WHERE u.id = :userId")
    Page<OrderItemResponseDto> findByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT new excluz.excluz.domain.order.orderItem.dto.response.OrderItemResponseDto(" +
            "o.id, oi.id, u.nickName, i.id, i.itemName, i.price, oi.item_quantity, o.createdAt) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN o.user u " +
            "JOIN oi.item i " +
            "JOIN i.store s " +
            "JOIN s.streamer st " +
            "WHERE st.id = :streamerId")
    Page<OrderItemResponseDto> findByStreamerId(@Param("streamerId") Integer streamerId, Pageable pageable);

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

    @Query("SELECT oi FROM OrderItem oi " +
            "JOIN FETCH oi.item " +
            "JOIN FETCH oi.order o " +
            "WHERE o.id = :orderId")
    List<OrderItem> findAllByOrderId(@Param("orderId") Integer orderId);
}

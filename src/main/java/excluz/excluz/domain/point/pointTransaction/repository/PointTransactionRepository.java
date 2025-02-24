package excluz.excluz.domain.point.pointTransaction.repository;

import excluz.excluz.common.entity.PointTransaction;
import excluz.excluz.domain.point.pointTransaction.dto.response.PointTransactionResponseDto;
import excluz.excluz.domain.user.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Integer> {

    @Query("SELECT new excluz.excluz.domain.point.pointTransaction.dto.response.PointTransactionResponseDto(" +
            "u.nickName, " +
            "CASE WHEN s IS NOT NULL AND st IS NOT NULL THEN st.nickName ELSE NULL END, " +
            "pt.transactionType, pt.amount, pt.createdAt) " +
            "FROM PointTransaction pt " +
            "LEFT JOIN pt.user u " +
            "LEFT JOIN pt.store s " +
            "LEFT JOIN s.streamer st " +
            "WHERE u.userRole = :userRole AND u.id = :userId")
    Page<PointTransactionResponseDto> findAllByUserRoleAndUserId(@Param("userRole") UserRole userRole,
                                                                 @Param("userId") Integer userId,
                                                                 Pageable pageable);

    @Query("SELECT new excluz.excluz.domain.point.pointTransaction.dto.response.PointTransactionResponseDto(" +
            "u.nickName, st.nickName, pt.transactionType, pt.amount, pt.createdAt) " +
            "FROM PointTransaction pt " +
            "LEFT JOIN pt.user u " +
            "LEFT JOIN pt.store s " +
            "LEFT JOIN s.streamer st " +
            "WHERE st.userRole = :userRole AND st.id = :streamerId")
    Page<PointTransactionResponseDto> findAllByUserRoleAndStreamerId(@Param("userRole") UserRole userRole,
                                                                     @Param("streamerId") Integer streamerId,
                                                                     Pageable pageable);


    @Query("SELECT pt FROM PointTransaction pt " +
            "LEFT JOIN FETCH pt.order " +
            "LEFT JOIN FETCH pt.user " +
            "LEFT JOIN FETCH pt.store " +
            "WHERE pt.order.id = :orderId")
    Optional<PointTransaction> findByOrderId(@Param("orderId") Integer orderId);
}

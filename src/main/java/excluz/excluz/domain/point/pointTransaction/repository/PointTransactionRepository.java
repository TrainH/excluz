package excluz.excluz.domain.point.pointTransaction.repository;

import excluz.excluz.common.entity.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Integer> {
    @Query("SELECT pt FROM PointTransaction pt " +
            "LEFT JOIN FETCH pt.user " +
            "WHERE pt.user.id = :userId")
    Page<PointTransaction> findAllByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT pt FROM PointTransaction pt " +
            "LEFT JOIN FETCH pt.store s " + // Store 엔티티를 먼저 조인
            "LEFT JOIN FETCH s.streamer " + // Streamer 엔티티를 그 후 조인
            "WHERE s.streamer.id = :streamerId")
    Page<PointTransaction> finAllByStreamerId(@Param("streamerId") Integer streamerId, Pageable pageable);
}

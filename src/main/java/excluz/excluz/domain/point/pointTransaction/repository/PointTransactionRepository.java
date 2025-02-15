package excluz.excluz.domain.point.pointTransaction.repository;

import excluz.excluz.common.entity.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Integer> {
    @Query("SELECT pt FROM PointTransaction pt " +
            "JOIN FETCH pt.user u " +
            "JOIN FETCH pt.store s " +
            "JOIN FETCH s.streamer st " +
            "LEFT JOIN FETCH pt.order o")
    Page<PointTransaction> findAllWithUserAndStoreAndStreamer(Pageable pageable);
}

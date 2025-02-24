package excluz.excluz.domain.ranking.storeRevenue.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import excluz.excluz.common.entity.Store;
import excluz.excluz.domain.point.pointTransaction.enums.TransactionType;

public interface StoreRevenueRepository extends JpaRepository<Store, Integer> {

	// 1. 전체 매출 순위 Top 10 조회 (매출액 비공개)
	@Query("SELECT pt.store.id, s.storeName, SUM(pt.amount) AS totalRevenue " +
		"FROM PointTransaction pt " +
		"JOIN pt.store s " +
		"WHERE pt.transactionType = :transactionType " +
		"AND FUNCTION('DATE_FORMAT', pt.createdAt, '%Y-%m') = :yearMonth " +
		"GROUP BY pt.store.id, s.storeName " +
		"ORDER BY totalRevenue DESC")
	List<Object[]> findTop10StoresByRevenue(@Param("transactionType") TransactionType transactionType,
		@Param("yearMonth") String yearMonth);

	// 2. 스트리머가 본인의 스토어 매출 조회 (월별)
	@Query("SELECT SUM(pt.amount) FROM PointTransaction pt " +
		"WHERE pt.store.id = :storeId " +
		"AND FUNCTION('DATE_FORMAT', pt.createdAt, '%Y-%m') = :yearMonth " +
		"AND pt.transactionType = :transactionType")
	Long getTotalRevenueByStoreIdAndMonth(@Param("storeId") int storeId,
		@Param("yearMonth") String yearMonth,
		@Param("transactionType") TransactionType transactionType);

	// 3. 특정 스토어의 월별 랭킹 조회
	@Query("SELECT COUNT(DISTINCT pt.store.id) + 1 " +
		"FROM PointTransaction pt " +
		"WHERE pt.transactionType = :transactionType " +
		"AND FUNCTION('DATE_FORMAT', pt.createdAt, '%Y-%m') = :yearMonth " +
		"AND (SELECT SUM(sub.amount) FROM PointTransaction sub " +
		"     WHERE sub.store.id = :storeId " +
		"     AND FUNCTION('DATE_FORMAT', sub.createdAt, '%Y-%m') = :yearMonth " +
		"     AND sub.transactionType = :transactionType) < SUM(pt.amount)")
	Integer getStoreRankByMonth(@Param("storeId") int storeId,
		@Param("yearMonth") String yearMonth,
		@Param("transactionType") TransactionType transactionType);

	// 4. 어드민 전체 매출 순위 조회 (월별, 페이징 처리)
	@Query(
		value = "SELECT pt.store_id, s.store_name, SUM(pt.amount) AS totalRevenue, " +
			"RANK() OVER (ORDER BY SUM(pt.amount) DESC) AS rank " +
			"FROM point_transactions pt " +
			"JOIN stores s ON pt.store_id = s.id " +
			"WHERE pt.transaction_type = :transactionType " +
			"AND DATE_FORMAT(pt.created_at, '%Y-%m') = :yearMonth " +
			"GROUP BY pt.store_id, s.store_name",
		countQuery = "SELECT COUNT(DISTINCT pt.store_id) " +
			"FROM point_transactions pt " +
			"WHERE pt.transaction_type = :transactionType " +
			"AND DATE_FORMAT(pt.created_at, '%Y-%m') = :yearMonth",
		nativeQuery = true
	)
	Page<Object[]> getAllStoreRankingsByMonth(@Param("yearMonth") String yearMonth,
		@Param("transactionType") TransactionType transactionType,
		Pageable pageable);

	// 5. 스토어 이름 조회
	@Query("SELECT s.storeName FROM Store s WHERE s.id = :storeId")
	String findStoreNameById(@Param("storeId") Integer storeId);

}

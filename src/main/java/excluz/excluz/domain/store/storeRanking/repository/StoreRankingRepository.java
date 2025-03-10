package excluz.excluz.domain.store.storeRanking.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.StoreRanking;
import excluz.excluz.domain.store.storeRevenue.enums.RevenuePeriod;

@Repository
public interface StoreRankingRepository extends JpaRepository<StoreRanking, Long> {
	// TOP 10 랭킹 조회 (매출 정보 제외)
	// 주어진 period와 날짜 범위에 대해 순위가 높은 순으로 10개의 기록만 조회
	@Query("SELECT sr FROM StoreRanking sr WHERE sr.rankingPeriod = :period "
		+ "AND sr.rankDate BETWEEN :startDate AND :endDate ORDER BY sr.rankPosition ASC")
	Page<StoreRanking> findTop10ByRankingPeriod(
		@Param("period") RevenuePeriod period, // 조회할 랭킹 기간 (DAY, MONTH, YEAR)
		@Param("startDate") LocalDateTime startDate, // 검색 시작 날짜
		@Param("endDate") LocalDateTime endDate, // 검색 종료 날짜
		Pageable pageable // 페이지 정보 (여기서는 10개를 요청)
	);

	// 특정 매장, 랭킹 기간, 날짜 범위에 맞는 정보 조회
	// 주어진 가게, period, 날짜 범위(startDate ~ endDate)에 해당하는 순위 데이터를 최근 순(날짜 내림차순)으로 조회
	@Query("SELECT sr FROM StoreRanking sr WHERE sr.store = :store AND "
		+ "sr.rankingPeriod = :period AND sr.rankDate BETWEEN :startDate AND :endDate ORDER BY sr.rankDate DESC")
	Page<StoreRanking> findByStoreAndPeriodAndRankDateBetween(
		@Param("store") Store store, // 조회할 가게
		@Param("period") RevenuePeriod period, // 조회할 랭킹 기간 (DAY, MONTH, YEAR)
		@Param("startDate") LocalDateTime startDate, // 검색 시작 날짜
		@Param("endDate") LocalDateTime endDate, // 검색 종료 날짜
		Pageable pageable // 페이지 정보 (몇 번째 페이지, 몇 개씩)
	);

	// 전체 매장, 랭킹 기간, 날짜 범위에 맞는 정보 조회
	// 주어진 period와 날짜 범위에 해당하는 모든 순위 데이터를 순위 오름차순(1등부터)으로 조회
	@Query("SELECT sr FROM StoreRanking sr WHERE sr.rankingPeriod = :period AND "
		+ "sr.rankDate BETWEEN :startDate AND :endDate ORDER BY sr.rankPosition ASC")
	Page<StoreRanking> findByPeriodAndRankDateBetween(
		@Param("period") RevenuePeriod period, // 조회할 랭킹 기간 (DAY, MONTH, YEAR)
		@Param("startDate") LocalDateTime startDate, // 검색 시작 날짜
		@Param("endDate") LocalDateTime endDate, // 검색 종료 날짜
		Pageable pageable // 페이지 정보 (몇 번째 페이지, 몇 개씩)
	);
}
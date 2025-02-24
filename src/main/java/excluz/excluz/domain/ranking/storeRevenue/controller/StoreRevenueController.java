package excluz.excluz.domain.ranking.storeRevenue.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.ranking.storeRevenue.dto.response.StoreRankingResponseDto;
import excluz.excluz.domain.ranking.storeRevenue.dto.response.StoreRankingTop10ResponseDto;
import excluz.excluz.domain.ranking.storeRevenue.service.StoreRevenueService;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store-ranking")
@RequiredArgsConstructor
public class StoreRevenueController {
	private final StoreRevenueService storeRevenueService;

	// 공식 랭킹 다건 조회 (Top 10): 인증 X, 누구나 접근 가능
	@GetMapping("/top10")
	public ResponseEntity<List<StoreRankingTop10ResponseDto>> getTop10Ranking(
		@RequestParam String yearMonth
	) {
		List<StoreRankingTop10ResponseDto> top10Ranking = storeRevenueService.getTop10StoreRevenue(yearMonth);
		return ResponseEntity.ok(top10Ranking);
	}

	// (스트리머) 특정 매출 및 순위 조회 (월별): 특정 스트리머의 스토어 매출 정보 및 순위 반환
	@GetMapping("/{storeId}")
	public ResponseEntity<StoreRankingResponseDto> getStoreRevenueForStreamer(
		@PathVariable int storeId,
		@RequestParam String yearMonth
	) {
		UserRole userRole = SecurityContextUtil.getUserRole();

		StoreRankingResponseDto response = storeRevenueService.getStoreRevenue(storeId, yearMonth, userRole);
		return ResponseEntity.ok(response);
	}

	// (어드민) 특정 스토어 매출 및 순위 조회 (월별): 특정 스트리머의 스토어 매출 정보 및 순위 반환
	@GetMapping("/admin/{storeId}")
	public ResponseEntity<StoreRankingResponseDto> getStoreRevenueForAdmin(
		@PathVariable int storeId,
		@RequestParam String yearMonth
	) {
		UserRole userRole = SecurityContextUtil.getUserRole();

		StoreRankingResponseDto response = storeRevenueService.getStoreRevenue(storeId, yearMonth, userRole);
		return ResponseEntity.ok(response);
	}

	// (어드민) 전체 스토어 매출 순위 조회 (월별, 페이징 처리)
	@GetMapping("/all")
	public ResponseEntity<Page<StoreRankingResponseDto>> getAllStoreRankingsForAdmin(
		@RequestParam String yearMonth,
		Pageable pageable
	) {
		UserRole userRole = SecurityContextUtil.getUserRole();

		Page<StoreRankingResponseDto> rankings = storeRevenueService.getAllStoreRankingsForAdmin(yearMonth, pageable, userRole);
		return ResponseEntity.ok(rankings);
	}
}

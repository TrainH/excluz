package excluz.excluz.domain.store.storeRanking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.store.storeRanking.dto.response.StoreRankingTop10ResponseDtoList;
import excluz.excluz.domain.store.storeRanking.service.StoreRankingService;
import excluz.excluz.domain.store.storeRevenue.enums.RevenuePeriod;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/store-ranking")
@RequiredArgsConstructor
public class StoreRankingController {
	private final StoreRankingService storeRankingService;
	private final StoreRepository storeRepository;

	// TOP 10 랭킹 조회 (매출 정보 제외)
	// RequestParam value의 enum(DAY, MONTH, YEAR)에 따라 동적으로 조회 가능
	// 요청 URL 예:
	@GetMapping("/top10")
	public ResponseEntity<StoreRankingTop10ResponseDtoList> getTop10StoreRankingList(
		@RequestParam(value = "period", defaultValue = "DAY") String period// "period" 값, 기본은 "DAY"
	) {
		// 문자열 period를 대소문자 구분 없이 RevenuePeriod(enum)으로 변환
		RevenuePeriod revenuePeriod = RevenuePeriod.valueOfIgnoreCase(period);

		// 서비스로부터 TOP 10 순위 데이터를 받아옴
		StoreRankingTop10ResponseDtoList response = storeRankingService.getTop10StoreRankingList(revenuePeriod);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
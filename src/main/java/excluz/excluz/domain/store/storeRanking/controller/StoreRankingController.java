package excluz.excluz.domain.store.storeRanking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.store.storeRanking.dto.response.StoreRankingResponseDtoList;
import excluz.excluz.domain.store.storeRanking.dto.response.StoreRankingTop10ResponseDtoList;
import excluz.excluz.domain.store.storeRanking.service.StoreRankingService;
import excluz.excluz.domain.store.storeRevenue.enums.RevenuePeriod;
import excluz.excluz.domain.user.enums.UserRole;
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

	// 역대 랭킹 조회 (스트리머: 자신의 가게 | 관리자: 특정 가게)
	// 요청 URL 예(스트리머): /api/v1/store-ranking/store/rankings?date=2025-03&period=MONTH&page=0&size=10
	// 요청 URL 예(관리자): /api/v1/store-ranking/store/rankings?date=2025-03&period=MONTH&page=0&size=10
	@GetMapping("/store/rankings")
	@PreAuthorize("hasAnyRole('STREAMER', 'ADMIN')") // 스트리머 or 관리자만 접근 허용
	public ResponseEntity<StoreRankingResponseDtoList> getStoreRankingList(
		@RequestParam(required = false) Integer storeId, // 관리자는 storeId 기입해야 함
		@RequestParam(value = "date", required = false) String date, // "yyyy-MM-dd" 또는 "yyyy-MM"
		@RequestParam(value = "period", defaultValue = "MONTH") String period, // "period" 값, 기본은 "MONTH"
		@RequestParam(defaultValue = "0") Integer page, // 페이지 번호
		@RequestParam(defaultValue = "10") Integer size // 페이지 크기 (한 페이지에 몇 개)
	) {
		// 날짜가 올바른 형식인지 확인
		validateDate(date);

		// period 문자열을 RevenuePeriod enum으로 변환
		RevenuePeriod revenuePeriod = RevenuePeriod.valueOfIgnoreCase(period);
		// 현재 로그인한 사용자의 ID와 역할을 가져옴
		Integer userId = SecurityContextUtil.getUserOrStreamerId();
		UserRole userRole = SecurityContextUtil.getUserRole();

		// targetStoreId 결정 로직
		Integer targetStoreId = resolveTargetStoreId(userId, userRole, storeId);

		// 서비스에서 해당 가게의 순위 정보를 조회
		StoreRankingResponseDtoList response = storeRankingService.getStoreRankingList(
			targetStoreId, revenuePeriod, date, page, size
		);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// targetStoreId 결정 로직 (개별 조회용)
	// 사용자의 역할에 따라 어떤 가게를 조회할지 결정하는 메서드
	private Integer resolveTargetStoreId(Integer userId, UserRole userRole, Integer storeId) {
		if (userRole == UserRole.STREAMER) {
			// 스트리머는 자신의 가게만 조회 가능
			return storeRepository.findStoreByStreamerIdAndNotDeleted(userId)
				.orElseThrow(() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND))
				.getId();
		} else if (userRole == UserRole.ADMIN) {
			// 관리자는 storeId가 반드시 필요함
			if (storeId == null) {
				throw new BadRequestException(ErrorCode.STORE_ID_REQUIRED);
			}
			// 입력받은 storeId가 실제로 존재하는지 확인
			storeRepository.findById(storeId)
				.orElseThrow(() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND));
			return storeId;
		} else {
			throw new ForbiddenException(ErrorCode.ACCESS_DENIED);
		}
	}

	// 날짜 형식 검증 메서드
	private void validateDate(String date) {
		if (date != null && !date.matches(
			"\\d{4}-\\d{2}(-\\d{2})?")) { // "yyyy-MM" 또는 "yyyy-MM-dd" (?는 바로 앞의 그룹이 옵션(선택적) 이라는 뜻)
			throw new BadRequestException(ErrorCode.INVALID_DATE_FORMAT);
		}
	}
}
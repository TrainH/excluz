package excluz.excluz.domain.ranking.storeRevenue.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.point.pointTransaction.enums.TransactionType;
import excluz.excluz.domain.ranking.storeRevenue.dto.response.StoreRankingResponseDto;
import excluz.excluz.domain.ranking.storeRevenue.dto.response.StoreRankingTop10ResponseDto;
import excluz.excluz.domain.ranking.storeRevenue.repository.StoreRevenueRepository;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreRevenueService {
	private final StoreRevenueRepository storeRevenueRepository;

	// 공식 랭킹 다건 조회 (Top 10): 인증 X, 누구나 접근 가능
	@Transactional(readOnly = true)
	public List<StoreRankingTop10ResponseDto> getTop10StoreRevenue(String yearMonth) {
		List<Object[]> results = storeRevenueRepository.findTop10StoresByRevenue(
			TransactionType.PURCHASE, yearMonth
		);

		// 결과가 없으면 예외 발생
		if (results.isEmpty()) {
			throw new NotFoundException(ErrorCode.STORE_NOT_FOUND);
		}

		return IntStream.range(0, results.size())
			.mapToObj(i -> {
				Object[] result = results.get(i);
				return new StoreRankingTop10ResponseDto(
					(Integer) result[0],  // storeId
					(String) result[1],   // storeName
					i + 1                 // rank
				);
			})
			.collect(Collectors.toList());
	}

	// (스트리머, 어드민) 특정 매출 및 순위 조회 (월별): 특정 스트리머의 스토어 매출 정보 및 순위 반환
	@Transactional(readOnly = true)
	public StoreRankingResponseDto getStoreRevenue(int storeId, String yearMonth, UserRole userRole) {
		// 스트리머와 어드민만 접근 가능
		if (userRole != UserRole.STREAMER && userRole != UserRole.ADMIN) {
			throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
		}

		Long totalRevenue = storeRevenueRepository.getTotalRevenueByStoreIdAndMonth(
			storeId, yearMonth, TransactionType.PURCHASE
		);

		Integer rank = storeRevenueRepository.getStoreRankByMonth(
			storeId, yearMonth, TransactionType.PURCHASE
		);

		// 스토어 이름 조회 (없으면 예외 발생)
		String storeName = storeRevenueRepository.findStoreNameById(storeId);
		if (storeName == null) {
			throw new NotFoundException(ErrorCode.STORE_NOT_FOUND);
		}

		return new StoreRankingResponseDto(
			storeId,
			storeName,
			Objects.requireNonNullElse(totalRevenue, 0L),
			Objects.requireNonNullElse(rank, 0)
		);
	}

	// (어드민) 전체 스토어 매출 순위 조회 (월별, 페이징 처리)
	@Transactional(readOnly = true)
	public Page<StoreRankingResponseDto> getAllStoreRankingsForAdmin(
		String yearMonth, Pageable pageable, UserRole userRole
	) {
		// 어드민만 접근 가능
		if (userRole != UserRole.ADMIN) {
			throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
		}

		Page<Object[]> results = storeRevenueRepository.getAllStoreRankingsByMonth(
			yearMonth, TransactionType.PURCHASE, pageable
		);

		// 결과가 비어있을 경우 예외 발생
		if (results.isEmpty()) {
			throw new NotFoundException(ErrorCode.STORE_NOT_FOUND);
		}

		return results.map(result -> new StoreRankingResponseDto(
			(Integer) result[0],   // storeId
			(String) result[1],    // storeName
			(Long) result[2],      // totalRevenue
			((Number) result[3]).intValue()  // rank
		));
	}
}
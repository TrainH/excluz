package excluz.excluz.domain.store.storeRanking.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.StoreRanking;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.store.storeRanking.dto.response.StoreRankingResponseDto;
import excluz.excluz.domain.store.storeRanking.dto.response.StoreRankingResponseDtoList;
import excluz.excluz.domain.store.storeRanking.dto.response.StoreRankingTop10ResponseDto;
import excluz.excluz.domain.store.storeRanking.dto.response.StoreRankingTop10ResponseDtoList;
import excluz.excluz.domain.store.storeRanking.repository.StoreRankingRepository;
import excluz.excluz.domain.store.storeRevenue.enums.RevenuePeriod;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreRankingService {
	private final StoreRankingRepository storeRankingRepository;
	private final StoreRepository storeRepository;

	// TOP 10 랭킹 조회 (매출 정보 제외)
	public StoreRankingTop10ResponseDtoList getTop10StoreRankingList(RevenuePeriod period) {
		// 리포지토리에서 지정된 period의 TOP 10 순위를 조회 (순위 오름차순 정렬)
		Page<StoreRanking> rankingPage = storeRankingRepository.findTop10ByRankingPeriod(period, PageRequest.of(0, 10));
		// 각 StoreRanking 엔티티를 StoreRankingTop10ResponseDto로 변환 (리스트)
		List<StoreRankingTop10ResponseDto> list = rankingPage.getContent().stream()
			.map(r -> new StoreRankingTop10ResponseDto(
				r.getStore().getId(),
				r.getStore().getStoreName(),
				r.getRankPosition()))
			.collect(Collectors.toList());
		return new StoreRankingTop10ResponseDtoList(list, rankingPage.getTotalElements());
	}

	// 역대 랭킹 조회 (스트리머: 자신의 가게 | 관리자: 특정 가게)
	public StoreRankingResponseDtoList getStoreRankingList(Integer storeId, RevenuePeriod revenuePeriod, String date,
		Integer page, Integer size) {
		// storeId가 유효한지 확인 (가게가 존재하는지 체크) -> 필수 검증 한 번 더 수행
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND));

		// 입력받은 날짜와 period 통해 조회할 날짜 범위 계산
		LocalDateTime[] range = getDateRange(revenuePeriod, date);

		// 해당 가게, period, 날짜 범위에 해당하는 순위 정보를 조회
		Page<StoreRanking> rankingPage = storeRankingRepository.findByStoreAndPeriodAndRankDateBetween(
			store, revenuePeriod, range[0], range[1], PageRequest.of(page, size)
		);
		return new StoreRankingResponseDtoList(toDtoList(rankingPage), rankingPage.getTotalElements());
	}

	// 날짜 범위 계산 메서드
	// "yyyy-MM-dd" 또는 "yyyy-MM" 형식의 문자열과 period에 따라 시작 시간과 종료 시간을 계산하여 배열로 반환
	private LocalDateTime[] getDateRange(RevenuePeriod period, String date) {
		LocalDateTime now = LocalDateTime.now();
		// "yyyy-MM-dd'T'HH:mm:ss" 형식의 포매터 (예: 2025-03-08T00:00:00)
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		LocalDateTime base;

		// date가 없으면 현재 날짜 기준
		if (date == null || date.isEmpty()) {
			base = switch (period) {
				case DAY -> now.truncatedTo(ChronoUnit.DAYS); // 오늘
				case MONTH -> now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS); // 이번 달 1일
				case YEAR -> now.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS); // 올해 1일
			};
		}
		// "yyyy-MM-dd" 형식 (예: 2025-03-08)
		else if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
			base = LocalDateTime.parse(date + "T00:00:00", formatter);
		}
		// "yyyy-MM" 형식 (예: 2025-03)
		else if (date.matches("\\d{4}-\\d{2}")) {
			base = LocalDateTime.parse(date + "-01T00:00:00", formatter);
		} else {
			throw new BadRequestException(ErrorCode.INVALID_DATE_FORMAT);
		}

		// period에 따라 시작일과 종료일을 계산하여 배열로 반환
		return switch (period) {
			case DAY -> new LocalDateTime[] {
				base.withHour(0).withMinute(0).withSecond(0), // 하루 시작 (00:00:00)
				base.withHour(23).withMinute(59).withSecond(59) // 하루 끝 (23:59:59)
			};
			case MONTH -> new LocalDateTime[] {
				base.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0), // 해당 달의 첫날 시작
				base.plusMonths(1).minusSeconds(1) // 다음 달의 첫날 바로 전 초 (해당 달의 끝)
			};
			case YEAR -> new LocalDateTime[] {
				base.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0), // 해당 년도의 1일 시작
				base.plusYears(1).minusSeconds(1) // 다음 해 1일 바로 전 초 (해당 년도의 끝)
			};
		};
	}

	// DTO 변환 메서드
	// StoreRanking 엔티티 페이지를 StoreRankingResponseDto 리스트로 변환하는 메서드
	private List<StoreRankingResponseDto> toDtoList(Page<StoreRanking> rankingPage) {
		return rankingPage.getContent().stream()
			.map(r -> new StoreRankingResponseDto(
				r.getStore().getId(),
				r.getStore().getStoreName(),
				r.getRevenue(),
				r.getRankPosition()))
			.collect(Collectors.toList());
	}
}
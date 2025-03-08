package excluz.excluz.domain.store.storeRanking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.StoreRanking;
import excluz.excluz.domain.store.store.repository.StoreRepository;
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
}
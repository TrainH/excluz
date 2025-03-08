package excluz.excluz.domain.store.storeRanking.dto.response;

import java.util.List;

import lombok.Getter;

@Getter
public class StoreRankingResponseDtoList {
	private final List<StoreRankingResponseDto> rankingList; // 랭킹 리스트
	private final Long totalCount; // 총 개수

	// 매개변수 4개 이하 -> 생성자 직접 작성
	public StoreRankingResponseDtoList(List<StoreRankingResponseDto> rankingList, Long totalCount) {
		this.rankingList = rankingList;
		this.totalCount = totalCount;
	}
}
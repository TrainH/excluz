package excluz.excluz.domain.store.storeRanking.dto.response;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;

@Getter
public class StoreRankingTop10ResponseDtoList implements Serializable {
	private final List<StoreRankingTop10ResponseDto> rankingList; // 랭킹 리스트
	private final Long totalCount; // 총 개수

	// 매개변수 4개 이하 -> 생성자 직접 작성
	public StoreRankingTop10ResponseDtoList(List<StoreRankingTop10ResponseDto> rankingList, Long totalCount) {
		this.rankingList = rankingList;
		this.totalCount = totalCount;
	}
}
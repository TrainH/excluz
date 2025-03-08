package excluz.excluz.domain.store.storeRanking.dto.response;

import lombok.Getter;

@Getter
public class StoreRankingTop10ResponseDto {
	private final Integer storeId; // 스토어 ID
	private final String storeName; // 스토어 이름
	private final Integer rankPosition; // 순위

	// 매개변수 4개 이하 -> 생성자 직접 작성
	public StoreRankingTop10ResponseDto(Integer storeId, String storeName, Integer rankPosition) {
		this.storeId = storeId;
		this.storeName = storeName;
		this.rankPosition = rankPosition;
	}
}
package excluz.excluz.domain.store.storeRanking.dto.response;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class StoreRankingResponseDto implements Serializable {
	private final Integer storeId; // 스토어 ID
	private final String storeName; // 스토어 이름
	private final Long revenue; // 총 매출
	private final Integer rankPosition; // 순위

	// 매개변수 4개 이하 -> 생성자 직접 작성
	public StoreRankingResponseDto(Integer storeId, String storeName, Long revenue, Integer rankPosition) {
		this.storeId = storeId;
		this.storeName = storeName;
		this.revenue = revenue;
		this.rankPosition = rankPosition;
	}
}
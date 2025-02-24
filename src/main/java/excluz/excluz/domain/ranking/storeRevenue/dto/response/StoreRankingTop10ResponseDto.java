package excluz.excluz.domain.ranking.storeRevenue.dto.response;

import lombok.Getter;

@Getter
public class StoreRankingTop10ResponseDto {
	private final Integer storeId;  // 스토어 ID
	private final String storeName;  // 스토어 이름
	private final int rank;  // 순위

	// 매개변수 4개 미만일 때는 직접 생성자 추가
	public StoreRankingTop10ResponseDto(Integer storeId, String storeName, int rank) {
		this.storeId = storeId;
		this.storeName = storeName;
		this.rank = rank;
	}
}
package excluz.excluz.domain.ranking.storeRevenue.dto.response;

import lombok.Getter;

@Getter
public class StoreRankingResponseDto {

	private final Integer storeId;     // 스토어 ID
	private final String storeName;    // 스토어 이름
	private final Long totalRevenue;   // 총 매출
	private final int rank;            // 순위

	// 매개변수 4개 이하 -> 생성자 직접 작성
	public StoreRankingResponseDto(Integer storeId, String storeName, Long totalRevenue, int rank) {
		this.storeId = storeId;
		this.storeName = storeName;
		this.totalRevenue = totalRevenue;
		this.rank = rank;
	}
}

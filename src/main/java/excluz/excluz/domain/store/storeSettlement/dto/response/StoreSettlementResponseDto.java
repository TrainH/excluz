package excluz.excluz.domain.store.storeSettlement.dto.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import excluz.excluz.common.entity.StoreSettlement;
import excluz.excluz.domain.store.storeRevenue.enums.RevenuePeriod;
import excluz.excluz.domain.store.storeSettlement.enums.FeeRate;
import excluz.excluz.domain.store.storeSettlement.enums.SettlementStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreSettlementResponseDto {

	private Integer id;
	private Integer storeId;
	private FeeRate platformFeeRate;
	private Long settlementAmount;
	private SettlementStatus settlementStatus;
	private RevenuePeriod settlementPeriod;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@QueryProjection
	public StoreSettlementResponseDto(StoreSettlement storeSettlement) {
		this.id = storeSettlement.getId();
		this.storeId = storeSettlement.getStoreId();
		this.platformFeeRate = storeSettlement.getPlatformFeeRate();
		this.settlementAmount = storeSettlement.getSettlementAmount();
		this.settlementStatus = storeSettlement.getSettlementStatus();
		this.settlementPeriod = storeSettlement.getSettlementPeriod();
		this.startDate = storeSettlement.getStartDate();
		this.endDate = storeSettlement.getEndDate();
		this.createdAt = storeSettlement.getCreatedAt();
		this.updatedAt = storeSettlement.getUpdatedAt();
	}
}

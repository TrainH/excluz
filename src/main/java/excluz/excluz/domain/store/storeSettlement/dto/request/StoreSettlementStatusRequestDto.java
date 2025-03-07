package excluz.excluz.domain.store.storeSettlement.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreSettlementStatusRequestDto {

	private String settlementStatus;

	public StoreSettlementStatusRequestDto(String settlementStatus) {
		this.settlementStatus = settlementStatus;
	}
}

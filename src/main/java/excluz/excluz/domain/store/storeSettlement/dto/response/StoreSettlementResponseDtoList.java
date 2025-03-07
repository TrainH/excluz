package excluz.excluz.domain.store.storeSettlement.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import excluz.excluz.common.entity.StoreSettlement;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreSettlementResponseDtoList {

	private List<StoreSettlementResponseDto> settlementResponseList;

	public StoreSettlementResponseDtoList(List<StoreSettlementResponseDto> settlementResponseList) {
		this.settlementResponseList=settlementResponseList;
	}
}

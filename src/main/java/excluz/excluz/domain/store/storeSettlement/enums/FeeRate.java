package excluz.excluz.domain.store.storeSettlement.enums;

import lombok.Getter;

@Getter
public enum FeeRate {
	LOW(0.05),
	MEDIUM(0.10),
	HIGH(0.15);

	private final Double feeRate;

	FeeRate(Double feeRate) {
		this.feeRate = feeRate;
	}
}

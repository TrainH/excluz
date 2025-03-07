package excluz.excluz.domain.store.storeSettlement.enums;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.error.ErrorCode;
import lombok.Getter;

@Getter
public enum SettlementStatus {
	WAITING("출금 요청 전"),
	HOLD("출금 보류"),
	PROCESSING("스트리머 출금 요청"),
	COMPLETED("출금 완료");

	private final String description;

	SettlementStatus(String description) {
		this.description = description;
	}

	public static SettlementStatus valueOfIgnoreCase(String status) {
		for (SettlementStatus settlementStatus : values()) {
			if (settlementStatus.name().equalsIgnoreCase(status)) {
				return settlementStatus;
			}
		}
		throw new BadRequestException(ErrorCode.SETTLEMENT_STATUS_NOT_MATCH);
	}

	// 정산 상태 변경 유효성 체크 (WAITING -> HOLD/PROCESSING -> COMPLETED 순서로 변경 가능하며 이전 순서로 되돌아갈 수 없음)
	public static boolean isValidStatusTransition(SettlementStatus currentStatus, SettlementStatus nextStatus) {
		Map<SettlementStatus, List<SettlementStatus>> validTransition = Map.of(
			SettlementStatus.WAITING, List.of(SettlementStatus.HOLD, SettlementStatus.PROCESSING),
			SettlementStatus.HOLD, List.of(SettlementStatus.PROCESSING, SettlementStatus.COMPLETED),
			SettlementStatus.PROCESSING, List.of(SettlementStatus.HOLD, SettlementStatus.COMPLETED)
		);

		return validTransition.getOrDefault(currentStatus, Collections.emptyList()).contains(nextStatus);
	}
}

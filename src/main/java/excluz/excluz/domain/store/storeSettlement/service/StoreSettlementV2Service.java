package excluz.excluz.domain.store.storeSettlement.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.StoreSettlement;
import excluz.excluz.common.exception.BusinessException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.store.storeRevenue.enums.RevenuePeriod;
import excluz.excluz.domain.store.storeSettlement.dto.request.StoreSettlementStatusRequestDto;
import excluz.excluz.domain.store.storeSettlement.dto.response.StoreSettlementResponseDto;
import excluz.excluz.domain.store.storeSettlement.dto.response.StoreSettlementResponseDtoList;
import excluz.excluz.domain.store.storeSettlement.enums.SettlementStatus;
import excluz.excluz.domain.store.storeSettlement.repository.StoreSettlementRepository;
import excluz.excluz.domain.store.storeSettlement.repository.StoreSettlementV2Repository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreSettlementV2Service {

	private StoreSettlementRepository settlementRepository;
	private StoreSettlementV2Repository settlementV2Repository;
	private StoreRepository storeRepository;

	@Transactional(readOnly = true)
	public StoreSettlementResponseDtoList getSettlementList(Integer storeId, Integer settlementId,
		LocalDateTime startDate, LocalDateTime endDate, String periodStr, Integer page, Integer size
	) {
		RevenuePeriod period = RevenuePeriod.valueOfIgnoreCase(periodStr);

		List<StoreSettlementResponseDto> responseDtoList = settlementV2Repository.findByPeriod(
			storeId, settlementId, startDate, endDate, period, page, size);

		if (responseDtoList.isEmpty()) {
			throw new NotFoundException(ErrorCode.SETTLEMENT_NOT_FOUND);
		}

		return new StoreSettlementResponseDtoList(responseDtoList);
	}

	@Transactional(readOnly = true)
	public StoreSettlementResponseDtoList getOwnSettlementList(Integer streamerId, Integer settlementId,
		LocalDateTime startDate, LocalDateTime endDate, String periodStr, Integer page, Integer size
	) {
		Store store = storeRepository.findStoreWithStreamer(streamerId).orElseThrow(
			() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND)
		);

		return getSettlementList(store.getId(), settlementId, startDate, endDate, periodStr, page, size);
	}

	@Transactional
	public StoreSettlementResponseDto updateSettlementStatus(Integer settlementId,
		StoreSettlementStatusRequestDto statusRequestDto
	) {
		SettlementStatus status = SettlementStatus.valueOfIgnoreCase(statusRequestDto.getSettlementStatus());

		StoreSettlement settlement = settlementRepository.findById(settlementId).orElseThrow(
			() -> new NotFoundException(ErrorCode.SETTLEMENT_NOT_FOUND)
		);

		// 비즈니스 규칙: 정산 완료된 건은 상태 변경 불가
		if (settlement.getSettlementStatus() == SettlementStatus.COMPLETED) {
			throw new BusinessException(ErrorCode.SETTLEMENT_ALREADY_COMPLETED);
		}

		// 비즈니스 규칙: 현재 상태 이전 단계로 변경 불가
		if (!SettlementStatus.isValidStatusTransition(settlement.getSettlementStatus(), status)) {
			throw new BusinessException(ErrorCode.INVALID_SETTLEMENT_STATUS_TRANSITION);
		}

		settlement.updateSettlementStatus(status);

		return new StoreSettlementResponseDto(settlement);
	}
}

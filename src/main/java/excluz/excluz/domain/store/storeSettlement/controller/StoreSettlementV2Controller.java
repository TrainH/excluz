package excluz.excluz.domain.store.storeSettlement.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.store.storeSettlement.dto.request.StoreSettlementStatusRequestDto;
import excluz.excluz.domain.store.storeSettlement.dto.response.StoreSettlementResponseDto;
import excluz.excluz.domain.store.storeSettlement.dto.response.StoreSettlementResponseDtoList;
import excluz.excluz.domain.store.storeSettlement.service.StoreSettlementV2Service;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/settlements")
public class StoreSettlementV2Controller {

	private final StoreSettlementV2Service settlementV2Service;

	@GetMapping("/me")
	@PreAuthorize("hasRole('STREAMER')")
	public ResponseEntity<StoreSettlementResponseDtoList> getOwnSettlementList(
		@RequestParam(required = false) Integer settlementId,
		@RequestParam(required = false)
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
		@RequestParam(required = false)
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
		@RequestParam(required = false) String period,
		@RequestParam(defaultValue = "0") Integer page,
		@RequestParam(defaultValue = "20") Integer size
	) {
		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();

		StoreSettlementResponseDtoList settlementResponseList = settlementV2Service.getOwnSettlementList(
			streamerId, settlementId, startDate, endDate, period, page, size);

		return new ResponseEntity<>(settlementResponseList, HttpStatus.OK);
	}

	@GetMapping()
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<StoreSettlementResponseDtoList> getSettlementList(
		@RequestParam(required = false) Integer settlementId,
		@RequestParam(required = false) Integer storeId,
		@RequestParam(required = false)
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
		@RequestParam(required = false)
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
		@RequestParam(required = false) String period,
		@RequestParam(defaultValue = "0") Integer page,
		@RequestParam(defaultValue = "20") Integer size
	) {
		StoreSettlementResponseDtoList settlementResponseList = settlementV2Service.getSettlementList(
			storeId, settlementId, startDate, endDate, period, page, size);

		return new ResponseEntity<>(settlementResponseList, HttpStatus.OK);
	}

	@PatchMapping()
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<StoreSettlementResponseDto> updateSettlementStatus(
		@RequestParam Integer settlementId,
		@RequestBody StoreSettlementStatusRequestDto statusRequestDto
	) {
		StoreSettlementResponseDto responseDto = settlementV2Service.updateSettlementStatus(settlementId, statusRequestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}

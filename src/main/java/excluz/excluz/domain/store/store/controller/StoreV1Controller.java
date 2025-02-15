package excluz.excluz.domain.store.store.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.domain.store.store.dto.request.StoreDeleteRequestDto;
import excluz.excluz.domain.store.store.dto.request.StoreRequestDto;
import excluz.excluz.domain.store.store.dto.request.StoreUpdateRequestDto;
import excluz.excluz.domain.store.store.dto.response.StoreDetailResponseDto;
import excluz.excluz.domain.store.store.dto.response.StoreResponseDto;
import excluz.excluz.domain.store.store.dto.response.StoreUpdateResponseDto;
import excluz.excluz.domain.store.store.service.StoreService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreV1Controller {

	private final StoreService storeService;

	@PostMapping()
	public ResponseEntity<Void> createStore(
		@AuthenticationPrincipal User user,
		@Valid @RequestBody StoreRequestDto storeRequestDto
	) {

		Integer streamerId = Integer.valueOf(user.getUsername());

		storeService.createStore(storeRequestDto, streamerId);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PatchMapping("/{storeId}/disable")
	public ResponseEntity<Void> deleteStore(
		@AuthenticationPrincipal User user,
		@PathVariable Integer storeId,
		@Valid @RequestBody StoreDeleteRequestDto deleteRequestDto
	) {

		Integer streamerId = Integer.valueOf(user.getUsername());

		storeService.deleteStore(deleteRequestDto, streamerId, storeId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PatchMapping("/{storeId}")
	public ResponseEntity<StoreUpdateResponseDto> updateStore(
		@PathVariable Integer storeId,
		@RequestBody StoreUpdateRequestDto requestDto
	) {
		StoreUpdateResponseDto responseDto = storeService.updateStore(storeId, requestDto);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@GetMapping()
	public ResponseEntity<Page<StoreResponseDto>> getStoreList(
		@RequestParam(required = false) String storeName,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {

		Page<StoreResponseDto> responseDtoList = storeService.getStoreList(storeName, page, size);

		return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
	}

	@GetMapping("/{storeId}")
	public ResponseEntity<StoreDetailResponseDto> getStoreById(
		@PathVariable Integer storeId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {

		StoreDetailResponseDto responseDto = storeService.getStoreById(storeId, page, size);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@GetMapping("/my-store")
	public ResponseEntity<StoreDetailResponseDto> getOwnedStore(
		@AuthenticationPrincipal User user,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Integer streamerId = Integer.valueOf(user.getUsername());

		StoreDetailResponseDto responseDto = storeService.getOwnedStoreById(streamerId, page, size);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}

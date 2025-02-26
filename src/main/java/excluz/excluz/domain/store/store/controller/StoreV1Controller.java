package excluz.excluz.domain.store.store.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.store.store.dto.request.StoreDeleteRequestDto;
import excluz.excluz.domain.store.store.dto.request.StoreRequestDto;
import excluz.excluz.domain.store.store.dto.request.StoreUpdateRequestDto;
import excluz.excluz.domain.store.store.dto.response.StoreDetailResponseDto;
import excluz.excluz.domain.store.store.dto.response.StoreNameResponseDto;
import excluz.excluz.domain.store.store.dto.response.StoreResponseDto;
import excluz.excluz.domain.store.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
@Slf4j
public class StoreV1Controller {

	private final StoreService storeService;

//	@PostMapping("/my-store")
//	@PreAuthorize("hasRole('STREAMER')")
//	public ResponseEntity<StoreResponseDto> createStore(
//		@Valid @RequestBody StoreRequestDto storeRequestDto
//	) {
//		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
//
//		StoreResponseDto responseDto = storeService.createStore(storeRequestDto, streamerId);
//
//		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
//	}

//	@DeleteMapping("/my-store/soft")
//	@PreAuthorize("hasRole('STREAMER')")
//	public ResponseEntity<Void> deleteStore(
//		@Valid @RequestBody StoreDeleteRequestDto deleteRequestDto
//	) {
//		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
//
//		storeService.deleteStore(deleteRequestDto, streamerId);
//
//		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//	}
//
//	@PatchMapping("/{storeId}")
//	@PreAuthorize("hasRole('STREAMER')")
//	public ResponseEntity<StoreResponseDto> updateStore(
//		@PathVariable Integer storeId,
//		@RequestBody(required = false) StoreUpdateRequestDto requestDto
//	) {
//		Integer userId = SecurityContextUtil.getUserOrStreamerId();
//
//		StoreResponseDto responseDto = storeService.updateStore(userId, storeId, requestDto);
//
//		return new ResponseEntity<>(responseDto, HttpStatus.OK);
//	}

//	@GetMapping()
//	public ResponseEntity<Page<StoreNameResponseDto>> getStoreList(
//		@RequestParam(required = false) String storeName,
//		@RequestParam(defaultValue = "0") int page,
//		@RequestParam(defaultValue = "10") int size
//	) {
//		Page<StoreNameResponseDto> responseDtoList = storeService.getStoreList(storeName, page, size);
//
//		return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
//	}
//
//	@GetMapping("/{storeId}")
//	public ResponseEntity<StoreDetailResponseDto> getStoreById(
//		@PathVariable Integer storeId,
//		@RequestParam(defaultValue = "0") int page,
//		@RequestParam(defaultValue = "10") int size
//	) {
//		StoreDetailResponseDto responseDto = storeService.getStoreById(storeId, page, size);
//
//		return new ResponseEntity<>(responseDto, HttpStatus.OK);
//	}
//
//	@GetMapping("/my-store")
//	public ResponseEntity<StoreDetailResponseDto> getOwnedStore(
//		@RequestParam(defaultValue = "0") int page,
//		@RequestParam(defaultValue = "10") int size
//	) {
//		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
//
//		StoreDetailResponseDto responseDto = storeService.getOwnedStoreById(streamerId, page, size);
//
//		return new ResponseEntity<>(responseDto, HttpStatus.OK);
//	}

	@GetMapping("/{storeId}")
	public String getStoreById(
			@PathVariable Integer storeId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model
	) {
		// Service 호출 (스토어 아이디로 조회)
		StoreDetailResponseDto responseDto = storeService.getStoreById(storeId, page, size);
		// Model에 담아서 뷰로 전달
		model.addAttribute("storeDetail", responseDto);

		// Thymeleaf 템플릿 "store/detail.html" 를 반환
		return "store/detail";
	}

	// 2) 스트리머가 소유한 스토어 조회 (마이 스토어)
	@GetMapping("/my-store")
	@PreAuthorize("hasRole('STREAMER')")
	public String getOwnedStore(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model
	) {
		// 현재 로그인된 스트리머 ID 가져오기
		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();

		// Service 호출 (소유 스토어 조회)
		log.info(streamerId.toString());
		StoreDetailResponseDto responseDto = storeService.getOwnedStoreById(streamerId, page, size);
		model.addAttribute("storeDetail", responseDto);

		return "store/my-store";
	}
}
package excluz.excluz.domain.store.item.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.store.item.dto.request.ItemCreateRequestDto;
import excluz.excluz.domain.store.item.dto.request.ItemUpdateRequestDto;
import excluz.excluz.domain.store.item.dto.response.ItemResponseDto;
import excluz.excluz.domain.store.item.dto.response.GetItemListResponseDto;
import excluz.excluz.domain.store.item.service.ItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemV1Controller {

	private final ItemService itemService;

	// 1) 등록 폼 페이지
	@GetMapping("/new")
	@PreAuthorize("hasRole('STREAMER')")
	public String showItemCreateForm(Model model) {
		// 폼에서 사용될 DTO 객체를 Model에 추가 (초기값 설정 가능)
		model.addAttribute("itemCreateRequestDto", new ItemCreateRequestDto());
		return "item/create/form";
	}

	// 2) 등록 처리 (Form Data)
	@PostMapping("/new")
	@PreAuthorize("hasRole('STREAMER')")
	public String createItemForm(
			@Valid @ModelAttribute ItemCreateRequestDto createRequestDto
	) {

		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();

		// 서비스 호출
		ItemResponseDto responseDto = itemService.createItem(createRequestDto, streamerId);

		// 성공 시 완료 페이지(또는 다른 페이지)로 이동
		// 여기서는 간단히 "등록 성공" 뷰를 띄우도록 함
		return "redirect:/api/v1/items/create-success";
	}

	// 3) 등록 성공 페이지
	@GetMapping("/create-success")
	public String showCreateSuccessPage() {
		return "item/create/success";
	}
}


//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/items")
//public class ItemV1Controller {
//
//	private final ItemService itemService;
//
//	@PostMapping()
//	@PreAuthorize("hasRole('STREAMER')")
//	public ResponseEntity<ItemResponseDto> createItem(
//		@Valid @RequestBody ItemCreateRequestDto createRequestDto
//	) {
//		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
//
//		ItemResponseDto responseDto = itemService.createItem(createRequestDto, streamerId);
//
//		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
//	}
//
//	@DeleteMapping("/{itemsId}/soft")
//	@PreAuthorize("hasRole('STREAMER')")
//	public ResponseEntity<Void> deleteItem(@PathVariable Integer itemsId) {
//		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
//
//		itemService.deleteItem(itemsId, streamerId);
//
//		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//	}
//
//	@PatchMapping("/{itemsId}")
//	@PreAuthorize("hasRole('STREAMER')")
//	public ResponseEntity<ItemResponseDto> updateItemInfo(
//		@PathVariable Integer itemsId,
//		@RequestBody(required = false) ItemUpdateRequestDto itemUpdateRequestDto
//	) {
//		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
//
//		ItemResponseDto responseDto = itemService.updateItemInfo(itemUpdateRequestDto, itemsId, streamerId);
//
//		return new ResponseEntity<>(responseDto, HttpStatus.OK);
//	}
//
//	@GetMapping("/{itemsId}")
//	public ResponseEntity<ItemResponseDto> getItemByItemId(@PathVariable Integer itemsId) {
//
//		ItemResponseDto responseDto = itemService.getItemById(itemsId);
//
//		return new ResponseEntity<>(responseDto, HttpStatus.OK);
//	}
//
//	@GetMapping()
//	public ResponseEntity<GetItemListResponseDto> getItemList(
//		@RequestParam(defaultValue = "0") int page,
//		@RequestParam(defaultValue = "10") int size,
//		@RequestParam(defaultValue = "0") Integer minPrice,
//		@RequestParam(defaultValue = "-1") Integer maxPrice,
//		@RequestParam(required = false) String itemName
//	) {
//		GetItemListResponseDto responseDto = itemService.getItemList(page, size, Math.max(minPrice, 0), maxPrice, itemName);
//
//		return new ResponseEntity<>(responseDto, HttpStatus.OK);
//	}
//}

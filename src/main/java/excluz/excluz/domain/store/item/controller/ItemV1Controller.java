package excluz.excluz.domain.store.item.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.store.item.dto.request.ItemCreateRequestDto;
import excluz.excluz.domain.store.item.dto.request.ItemUpdateRequestDto;
import excluz.excluz.domain.store.item.dto.response.ItemResponseDto;
import excluz.excluz.domain.store.item.service.ItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemV1Controller {

	private final ItemService itemService;

	@PostMapping()
	@PreAuthorize("hasRole('STREAMER')")
	public ResponseEntity<ItemResponseDto> createItem(
		@Valid @RequestBody ItemCreateRequestDto createRequestDto
	) {
		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();

		ItemResponseDto responseDto = itemService.createItem(createRequestDto, streamerId);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	@DeleteMapping("/{itemsId}/soft")
	@PreAuthorize("hasRole('STREAMER')")
	public ResponseEntity<Void> deleteItem(@PathVariable Integer itemsId) {
		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();

		itemService.deleteItem(itemsId, streamerId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PatchMapping("/{itemsId}")
	@PreAuthorize("hasRole('STREAMER')")
	public ResponseEntity<ItemResponseDto> updateItemInfo(
		@PathVariable Integer itemsId,
		@RequestBody(required = false) ItemUpdateRequestDto itemUpdateRequestDto
	) {
		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();

		ItemResponseDto responseDto = itemService.updateItemInfo(itemUpdateRequestDto, itemsId, streamerId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@GetMapping("/{itemsId}")
	public ResponseEntity<ItemResponseDto> getItemByItemId(@PathVariable Integer itemsId) {

		ItemResponseDto responseDto = itemService.getItemById(itemsId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@GetMapping()
	public ResponseEntity<Page<ItemResponseDto>> getItemList(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false, defaultValue = "0") Integer minPrice,
		@RequestParam(required = false, defaultValue = "-1") Integer maxPrice,
		@RequestParam(required = false) String itemName
	) {
		Page<ItemResponseDto> itemResponseDtoList = itemService.getItemList(page, size, minPrice, maxPrice, itemName);

		return new ResponseEntity<>(itemResponseDtoList, HttpStatus.OK);
	}
}

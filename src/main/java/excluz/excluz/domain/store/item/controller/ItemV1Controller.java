package excluz.excluz.domain.store.item.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.domain.store.item.dto.request.ItemCreateRequestDto;
import excluz.excluz.domain.store.item.dto.request.ItemUpdateRequestDto;
import excluz.excluz.domain.store.item.dto.response.ItemResponseDto;
import excluz.excluz.domain.store.item.service.ItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemV1Controller {

	private final ItemService itemService;

	@PostMapping()
	public ResponseEntity<Void> createItem(
		/* TODO JWT 어노테이션 활용으로 수정 예정 */
		HttpServletRequest request,
		@Valid @RequestBody ItemCreateRequestDto createRequestDto
	) {

		/* TODO JWT 토큰에서의 정보 추출 방식 추후 수정 예정 */
		itemService.createItem(createRequestDto, (Integer)request.getAttribute("streamerId"));

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@DeleteMapping("/{itemsId}")
	public ResponseEntity<Void> deleteItem(@PathVariable Integer itemsId) {

		itemService.deleteItem(itemsId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PatchMapping("/{itemsId}")
	public ResponseEntity<ItemResponseDto> updateItemInfo(
		@PathVariable Integer itemsId,
		@RequestBody(required = false) ItemUpdateRequestDto itemUpdateRequestDto
	) {

		ItemResponseDto responseDto = itemService.updateItemInfo(itemUpdateRequestDto, itemsId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}

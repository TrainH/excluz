package excluz.excluz.domain.store.item.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.domain.store.item.dto.response.GetItemListResponseDtoV2;
import excluz.excluz.domain.store.item.service.ItemV2Service;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/items")
public class ItemV2Controller {

	private final ItemV2Service itemV2Service;

	@GetMapping()
	public ResponseEntity<GetItemListResponseDtoV2> getItemList(
		@RequestParam(defaultValue = "0") Integer minPrice,
		@RequestParam(defaultValue = "2147483647") Integer maxPrice,
		@RequestParam(required = false) String itemName,
		@RequestParam(required = false) Integer cursor,
		@RequestParam(defaultValue = "20") int limit
	) {
		GetItemListResponseDtoV2 responseDto = itemV2Service.getItemList(minPrice, maxPrice, itemName, cursor, limit);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}

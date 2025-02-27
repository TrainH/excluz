package excluz.excluz.domain.store.item.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.store.item.dto.request.ItemCreateRequestDto;
import excluz.excluz.domain.store.item.dto.request.ItemUpdateRequestDto;
import excluz.excluz.domain.store.item.dto.response.GetItemListResponseDto;
import excluz.excluz.domain.store.item.dto.response.ItemResponseDto;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	private final StoreRepository storeRepository;

	@Transactional
	public ItemResponseDto createItem(ItemCreateRequestDto createRequestDto, Integer streamerId) {
		Store store = storeRepository.findStoreWithStreamer(streamerId).orElseThrow(
			() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND)
		);

		Item item = Item.builder()
			.store(store)
			.itemName(createRequestDto.getItemName())
			.explanation(createRequestDto.getExplanation())
			.price(createRequestDto.getPrice())
			.remainingQuantity(createRequestDto.getRemainingQuantity())
			.build();

		Item savedItem = itemRepository.save(item);

		return ItemResponseDto.from(savedItem);
	}

	@Transactional
	public void deleteItem(Integer itemsId, Integer streamerId) {
		Item item = findItemByIdAndNotDeleted(itemsId);

		/*TODO*/
		if (!item.getStore().getStreamer().getId().equals(streamerId)) {
			throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
		}

		item.updateIsDeleted(true); // softDelete()
	}

	@Transactional
	public ItemResponseDto updateItemInfo(ItemUpdateRequestDto itemUpdateRequestDto, Integer itemsId, Integer streamerId) {
		Item item = findItemByIdAndNotDeleted(itemsId);

		// 아이템 수정 권한이 있는 회원인지 확인 /* TODO 수정 */
		if (!item.getStore().getStreamer().getId().equals(streamerId)) {
			throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
		}

		item.updateItem(itemUpdateRequestDto.getItemName(),
			itemUpdateRequestDto.getExplanation(),
			itemUpdateRequestDto.getPrice(),
			itemUpdateRequestDto.getRemainingQuantity());

		return ItemResponseDto.from(item);
	}

	@Transactional(readOnly = true)
	public ItemResponseDto getItemById(Integer itemsId) { //getItemDTOById
		return ItemResponseDto.from(findItemByIdAndNotDeleted(itemsId));
	}

	@Transactional(readOnly = true)
	public GetItemListResponseDto getItemList(int page, int size, Integer minPrice, Integer maxPrice, String itemName) {

		Pageable pageable = PageRequest.of(Math.max(page, 0), size);
		// int newMinPrice=minPrice, newMaxPrice=maxPrice;
		// Optional<Integer> highestPrice = itemRepository.findHighestItemPrice();
		//
		// // 유효 가격 범위로 값 재설정
		// if (minPrice == Integer.MAX_VALUE) {
		// 	newMinPrice = highestPrice.orElse(0);
		// 	newMaxPrice = Integer.MAX_VALUE;
		// }
		// else if (maxPrice <= minPrice) {
		// 	newMaxPrice = highestPrice.orElse(minPrice + 1);
		// }

		Page<Item> items = itemRepository.findByPriceWithItemName(pageable, newMinPrice, newMaxPrice, itemName);
		Page<ItemResponseDto> responseDtoPage = items.map(ItemResponseDto::from);

		return new GetItemListResponseDto(newMinPrice, newMaxPrice, responseDtoPage);
	}

	// 삭제 되지 않은 아이템만 조회하는 메서드
	private Item findItemByIdAndNotDeleted(Integer itemsId) {
		return itemRepository.findItemByIdAndNotDeleted(itemsId).orElseThrow(
			() -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND)
		);
	}
}

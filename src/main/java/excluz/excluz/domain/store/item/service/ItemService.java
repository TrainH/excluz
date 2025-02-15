package excluz.excluz.domain.store.item.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.store.item.dto.request.ItemCreateRequestDto;
import excluz.excluz.domain.store.item.dto.request.ItemUpdateRequestDto;
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
	public void createItem(ItemCreateRequestDto createRequestDto, Integer streamerId) {
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

		itemRepository.save(item);
	}

	@Transactional
	public void deleteItem(Integer itemsId) {
		Item item = findItemByIdAndNotDeleted(itemsId);
		item.updateIsDeleted(true);
	}

	@Transactional
	public ItemResponseDto updateItemInfo(ItemUpdateRequestDto itemUpdateRequestDto, Integer itemsId) {
		Item item = findItemByIdAndNotDeleted(itemsId);

		item.updateItem(itemUpdateRequestDto.getItemName(),
			itemUpdateRequestDto.getExplanation(),
			itemUpdateRequestDto.getPrice(),
			itemUpdateRequestDto.getRemainingQuantity());

		return ItemResponseDto.from(item);
	}

	@Transactional(readOnly = true)
	public ItemResponseDto getItemById(Integer itemsId) {
		return ItemResponseDto.from(findItemByIdAndNotDeleted(itemsId));
	}

	@Transactional(readOnly = true)
	public Page<ItemResponseDto> getItemList(int page, int size, Integer minPrice, Integer maxPrice, String itemName) {

		Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
		int newMinPrice=minPrice, newMaxPrice=maxPrice;
		Optional<Integer> highestPrice = itemRepository.findHighestItemPrice();

		// 유효 가격 범위로 값 재설정
		if (minPrice == Integer.MAX_VALUE) {
			newMinPrice = highestPrice.orElse(0);
			newMaxPrice = Integer.MAX_VALUE;
		}
		if (maxPrice <= minPrice) {
			newMaxPrice = highestPrice.orElse(minPrice + 1);
		}

		Page<Item> items = itemRepository.findByPriceWithItemName(pageable, newMinPrice, newMaxPrice, itemName);

		return items.map(ItemResponseDto::from);
	}

	// 삭제 되지 않은 아이템만 조회하는 메서드
	private Item findItemByIdAndNotDeleted(Integer itemsId) {
		return itemRepository.findItemByIdAndNotDeleted(itemsId).orElseThrow(
			() -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND)
		);
	}
}

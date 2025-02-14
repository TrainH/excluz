package excluz.excluz.domain.store.item.service;

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
		Item item = findItemById(itemsId);

		item.updateIsDeleted(true);
	}

	@Transactional
	public ItemResponseDto updateItemInfo(ItemUpdateRequestDto itemUpdateRequestDto, Integer itemsId) {
		Item item = findItemById(itemsId);

		item.updateItem(itemUpdateRequestDto.getItemName(),
			itemUpdateRequestDto.getExplanation(),
			itemUpdateRequestDto.getPrice(),
			itemUpdateRequestDto.getRemainingQuantity());

		return ItemResponseDto.from(item);
	}


	private Item findItemById(Integer itemsId) {
		return itemRepository.findById(itemsId).orElseThrow(
			() -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND)
		);
	}
}

package excluz.excluz.domain.store.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.store.item.dto.request.ItemCreateRequestDto;
import excluz.excluz.domain.store.item.dto.request.ItemUpdateRequestDto;
import excluz.excluz.domain.store.item.dto.response.GetItemListResponseDto;
import excluz.excluz.domain.store.item.dto.response.ItemListResponseDto;
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

		if (!item.getStore().getStreamer().getId().equals(streamerId)) {
			throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
		}

		item.updateIsDeleted(true);
	}

	@Transactional
	public ItemResponseDto updateItemInfo(ItemUpdateRequestDto itemUpdateRequestDto, Integer itemsId, Integer streamerId) {
		Item item = findItemByIdAndNotDeleted(itemsId);

		// 아이템 수정 권한이 있는 회원인지 확인
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
	public ItemResponseDto getItemById(Integer itemsId) {
		return ItemResponseDto.from(findItemByIdAndNotDeleted(itemsId));
	}

	@Transactional(readOnly = true)
	public GetItemListResponseDto getItemList(Pageable pageable,Integer minPrice, Integer maxPrice, String itemName) {
		if (!isValidPrice(minPrice,maxPrice)) {
			throw new BadRequestException(ErrorCode.INVALID_ITEM_PRICE);
		}

		Page<Item> items = itemRepository.findByPriceWithItemName(pageable, minPrice, maxPrice, itemName);
		Page<ItemListResponseDto> responseDtoPage = items.map(ItemListResponseDto::from);

		return new GetItemListResponseDto(minPrice, maxPrice, responseDtoPage);
	}

	private boolean isValidPrice(Integer minPrice, Integer maxPrice) {
		return minPrice >= 0 && maxPrice >= 0 && (maxPrice >= minPrice);
	}

	// 삭제 되지 않은 아이템만 조회하는 메서드
	@Transactional(readOnly = true)
	public Item findItemByIdAndNotDeleted(Integer itemsId) {
		return itemRepository.findItemByIdAndNotDeleted(itemsId).orElseThrow(
			() -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND)
		);
	}
}

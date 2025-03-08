package excluz.excluz.domain.store.item.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.Item;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.store.item.dto.response.GetItemListResponseDtoV2;
import excluz.excluz.domain.store.item.dto.response.ItemListResponseDto;
import excluz.excluz.domain.store.item.repository.ItemV2Repository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ItemV2Service {

	private final ItemV2Repository itemV2Repository;

	@Transactional(readOnly = true)
	public GetItemListResponseDtoV2 getItemList(
		Integer minPrice, Integer maxPrice,	String itemName, Integer storeId, Integer cursor, int limit
	) {
		if (!isValidPrice(minPrice, maxPrice)) {
			throw new BadRequestException(ErrorCode.INVALID_ITEM_PRICE);
		}

		// 다음 페이지 존재 여부 확인
		List<Item> itemList = itemV2Repository.findByPriceWithItemNameV2(
			minPrice, maxPrice,	itemName, storeId, cursor, limit + 1);

		boolean hasNext = itemList.size() > limit;
		if (hasNext) {
			itemList.remove(itemList.size() - 1); // 다음 페이지 확인용 추가 데이터 제거
		}

		// 다음 페이지가 있다면 마지막 아이템의 id를 nextCursor로 설정 (정렬 기준 = id 내림차순)
		Integer nextCursor = hasNext ? itemList.get(itemList.size() - 1).getId() : null;

		List<ItemListResponseDto> itemListResponseDtoList = itemList.stream()
			.map(ItemListResponseDto::from)
			.toList();

		return new GetItemListResponseDtoV2(minPrice, maxPrice, itemListResponseDtoList, nextCursor);
	}

	private boolean isValidPrice(Integer minPrice, Integer maxPrice) {
		return minPrice >= 0 && maxPrice >= 0 && (maxPrice >= minPrice);
	}
}

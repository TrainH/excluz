package excluz.excluz.domain.store.item.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.domain.store.item.dto.response.GetItemListResponseDtoV2;
import excluz.excluz.domain.store.item.repository.ItemV2Repository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ItemV4Service {

	private final ItemV2Repository itemV2Repository;
	private final ItemV2Service itemV2Service;

	@Cacheable(value = "ITEM_LIST_CACHE_V4",
		key = "(#storeId != null ? #storeId : 'null') + '_' +"
			+ "(#itemName != null ? #itemName.trim().replaceAll('\\s', '').toLowerCase().hashCode() : 'null') + '_' +"
			+ " #cursor + '_' + #limit",
		cacheManager = "redisCacheManager"
	)
	@Transactional(readOnly = true)
	public GetItemListResponseDtoV2 getItemList(
		Integer minPrice, Integer maxPrice,	String itemName, Integer storeId, Integer cursor, int limit
	) {
		return itemV2Service.getItemList(minPrice, maxPrice, itemName, storeId, cursor, limit);
	}
}

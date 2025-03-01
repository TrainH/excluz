package excluz.excluz.domain.store.item.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetItemListResponseDtoV2 {

	private Integer minPrice;
	private Integer maxPrice;
	private List<ItemListResponseDto> itemList;
	private Integer nextCursor;

	@Builder
	public GetItemListResponseDtoV2(
		Integer minPrice,
		Integer maxPrice,
		List<ItemListResponseDto> itemList,
		Integer nextCursor
	) {
		this.minPrice=minPrice;
		this.maxPrice=maxPrice;
		this.itemList=itemList;
		this.nextCursor=nextCursor;
	}
}

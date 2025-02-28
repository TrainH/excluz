package excluz.excluz.domain.store.item.dto.response;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetItemListResponseDto {

	private Integer minPrice;
	private Integer maxPrice;
	private Page<ItemListResponseDto> itemList;

	public GetItemListResponseDto(Integer minPrice, Integer maxPrice, Page<ItemListResponseDto> itemList) {
		this.minPrice=minPrice;
		this.maxPrice=maxPrice;
		this.itemList=itemList;
	}
}

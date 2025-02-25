package excluz.excluz.domain.store.item.dto.response;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetItemListResponseDto {

	private Integer minPrice;
	private Integer maxPrice;
	private Page<ItemResponseDto> responseDtoPage;

	public GetItemListResponseDto(Integer minPrice, Integer maxPrice, Page<ItemResponseDto> responseDtoPage) {
		this.minPrice=minPrice;
		this.maxPrice=maxPrice;
		this.responseDtoPage=responseDtoPage;
	}
}

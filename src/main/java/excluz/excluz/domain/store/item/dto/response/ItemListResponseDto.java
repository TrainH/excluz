package excluz.excluz.domain.store.item.dto.response;

import excluz.excluz.common.entity.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemListResponseDto {

	private Integer itemId;
	private String itemName;
	private Integer price;

	@Builder
	public ItemListResponseDto(
		Integer itemId,
		String itemName,
		Integer price
	) {
		this.itemId = itemId;
		this.itemName=itemName;
		this.price=price;
	}

	public static ItemListResponseDto from(Item item) {
		return new ItemListResponseDto(
			item.getId(),
			item.getItemName(),
			item.getPrice()
		);
	}
}

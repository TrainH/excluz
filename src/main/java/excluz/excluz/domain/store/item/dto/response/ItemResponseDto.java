package excluz.excluz.domain.store.item.dto.response;

import excluz.excluz.common.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {

	private String itemName;
	private String explanation;
	private Integer price;
	private Integer remainingQuantity;

	public static ItemResponseDto from(Item item) {
		return new ItemResponseDto(
			item.getItemName(),
			item.getExplanation(),
			item.getPrice(),
			item.getRemainingQuantity());
	}
}

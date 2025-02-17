package excluz.excluz.domain.store.item.dto.response;

import excluz.excluz.common.entity.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemResponseDto {

	private String itemName;
	private String explanation;
	private Integer price;
	private Integer remainingQuantity;

	@Builder
	public ItemResponseDto(
		String itemName,
		String explanation,
		Integer price,
		Integer remainingQuantity
	) {
		this.itemName=itemName;
		this.explanation=explanation;
		this.price=price;
		this.remainingQuantity=remainingQuantity;
	}

	public static ItemResponseDto from(Item item) {
		return new ItemResponseDto(
			item.getItemName(),
			item.getExplanation(),
			item.getPrice(),
			item.getRemainingQuantity());
	}
}

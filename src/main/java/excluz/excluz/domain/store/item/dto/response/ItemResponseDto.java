package excluz.excluz.domain.store.item.dto.response;

import excluz.excluz.common.entity.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemResponseDto {

	private Integer itemId;
	private String itemName;
	private String explanation;
	private Integer price;
	private Integer remainingQuantity;

	@Builder
	public ItemResponseDto(
		Integer itemId,
		String itemName,
		String explanation,
		Integer price,
		Integer remainingQuantity
	) {
		this.itemId = itemId;
		this.itemName=itemName;
		this.explanation=explanation;
		this.price=price;
		this.remainingQuantity=remainingQuantity;
	}

	public static ItemResponseDto from(Item item) {
		return new ItemResponseDto(
			item.getId(),
			item.getItemName(),
			item.getExplanation(),
			item.getPrice(),
			item.getRemainingQuantity());
	}
}

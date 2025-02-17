package excluz.excluz.domain.store.item.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemUpdateRequestDto {

	private String itemName;
	private String explanation;
	private Integer price;
	private Integer remainingQuantity;

	@Builder
	public ItemUpdateRequestDto(
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
}

package excluz.excluz.domain.store.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateRequestDto {

	private String itemName;
	private String explanation;
	private Integer price;
	private Integer remainingQuantity;
}

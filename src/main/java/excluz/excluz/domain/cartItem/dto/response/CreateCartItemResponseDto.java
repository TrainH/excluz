package excluz.excluz.domain.cartItem.dto.response;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateCartItemResponseDto implements Serializable {
	// 카트 아이템 아이디
	private final Integer cartItemId;

	// 아이템 아이디
	private final Integer itemId;

	// 스토어 아이디
	private final Integer storeId;

	// 개수
	private final Integer quantity;

	// 개당 가격(아이템 가격)
	private final Integer itemPrice;

	// 총 가격 (개당 가격 * 개수)
	private final Integer totalItemPrice;

	@Builder
	public CreateCartItemResponseDto(Integer cartItemId, Integer itemId, Integer storeId, Integer quantity, Integer itemPrice) {
		this.cartItemId = cartItemId;
		this.itemId = itemId;
		this.storeId = storeId;
		this.quantity = quantity;
		this.itemPrice = itemPrice;
		this.totalItemPrice = itemPrice * quantity; // 개수 반영한 총 가격
	}
}

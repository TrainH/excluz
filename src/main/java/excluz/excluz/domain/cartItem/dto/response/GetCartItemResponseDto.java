package excluz.excluz.domain.cartItem.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetCartItemResponseDto {
	// 카트 아이템 아이디
	private final Integer cartItemId;

	// 개수
	private final Integer quantity;

	// 개당 가격(아이템 가격)
	private final Integer itemPrice;

	// 총 가격 (개당 가격 * 개수)
	private final Integer totalItemPrice;

	@Builder
	public GetCartItemResponseDto(Integer cartItemId, Integer quantity, Integer itemPrice) {
		this.cartItemId = cartItemId;
		this.quantity = quantity;
		this.itemPrice = itemPrice;
		this.totalItemPrice = itemPrice * quantity; // 개수 반영한 총 가격
	}
}

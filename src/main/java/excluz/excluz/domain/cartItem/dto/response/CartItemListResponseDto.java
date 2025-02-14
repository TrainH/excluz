package excluz.excluz.domain.cartItem.dto.response;

import lombok.Getter;
import java.util.List;

@Getter
public class CartItemListResponseDto {
	// 전체 장바구니 가격
	private final Integer totalPrice;

	// 개별 아이템 리스트
	private final List<GetCartItemResponseDto> cartItemList;

	public CartItemListResponseDto(List<GetCartItemResponseDto> cartItemList) {
		this.cartItemList = cartItemList;
		this.totalPrice = cartItemList.stream()
			.mapToInt(GetCartItemResponseDto::getTotalItemPrice) // 개별 아이템 총 가격 합산
			.sum();
	}
}

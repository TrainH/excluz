package excluz.excluz.domain.cartItem.dto.response;

import lombok.Getter;
import java.util.List;

import org.springframework.data.domain.Page;

@Getter
public class CartItemListResponseDto {
	// 전체 장바구니 가격
	private final Integer totalPrice;

	// 개별 아이템 리스트
	private final Page<GetCartItemResponseDto> cartItemList;

	public CartItemListResponseDto(Page<GetCartItemResponseDto> cartItemList) {
		this.cartItemList = cartItemList;
		this.totalPrice = cartItemList.stream()
			.mapToInt(GetCartItemResponseDto::getTotalItemPrice) // 개별 아이템 총 가격 합산
			.sum();
	}
}

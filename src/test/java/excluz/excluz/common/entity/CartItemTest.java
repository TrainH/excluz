package excluz.excluz.common.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CartItemTest {

	@Test
	@DisplayName("CartItem의 개수가 잘 업데이트된다.")
	void updateQuantity() {
		// given
		CartItem cartItem = new CartItem(
			null,  // user: null (사용자 정보 없음)
			null,  // item: null (상품 정보 없음)
			10     // quantity: 10 (초기 장바구니 속 특정 아이템 개수)
		);

		// when
		cartItem.updateQuantity(9); // 장바구니 속 특정 아이템 개수를 9로 변경

		// then
		Assertions.assertThat(cartItem.getQuantity())
			.isEqualTo(9);  // expected: 9 (기대한 값)
	}
}
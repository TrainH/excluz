package excluz.excluz.common.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CartItemTest {

	@Test
	@DisplayName("CartItem의 개수가 잘 업데이트된다.")
	void updateQuantity() {
		// given
		CartItem cartItem = new CartItem(null, null, 10);

		// when
		cartItem.updateQuantity(9);

		// then
		Assertions.assertThat(cartItem.getQuantity()).isEqualTo(9);
	}
}
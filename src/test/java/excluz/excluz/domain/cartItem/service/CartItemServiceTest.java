package excluz.excluz.domain.cartItem.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import excluz.excluz.common.entity.CartItem;
import excluz.excluz.common.entity.Item;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.domain.cartItem.dto.request.UpdateCartItemQuantityRequestDto;
import excluz.excluz.domain.cartItem.repository.CartItemRepository;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

	@Mock
	private CartItemRepository cartItemRepository;

	@InjectMocks
	private CartItemService cartItemService;

	@Test
	@DisplayName("요청된 개수가 재고보다 많은 경우 예외가 발생한다")
	void updateCartItemQuantity() {
		// given
		Item item = new Item(
			null,
			"itemName",
			"test",
			100,
			10
		);
		CartItem cartItem = new CartItem(
			null,
			item,
			10
		);

		Mockito.when(cartItemRepository.findByIdAndUserId(Mockito.any(), Mockito.any()))
			.thenReturn(Optional.of(cartItem));

		UpdateCartItemQuantityRequestDto cartItemQuantityRequestDto = new UpdateCartItemQuantityRequestDto(100);

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.updateCartItemQuantity(
				null,
				null,
				cartItemQuantityRequestDto
			))
			.isInstanceOf(BadRequestException.class);
	}
}
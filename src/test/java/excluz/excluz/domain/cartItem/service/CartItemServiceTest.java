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
			null,         // store: null (스토어 정보 없음)
			"itemName",   // itemName: "itemName" (상품명)
			"test",       // explanation: "test" (설명)
			100,          // price: 100 (상품 가격)
			10            // remainingQuantity: 10 (재고 개수)
		);
		CartItem cartItem = new CartItem(
			null,  // user: null (사용자 정보 없음)
			item,  // item: 위에서 생성한 Item 객체
			10     // quantity: 10 (현재 장바구니에 담긴 수량)
		);

		Mockito.when(cartItemRepository.findByIdAndUserId(Mockito.any(), Mockito.any()))
			.thenReturn(Optional.of(cartItem));

		UpdateCartItemQuantityRequestDto cartItemQuantityRequestDto = new UpdateCartItemQuantityRequestDto(100);

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.updateCartItemQuantity(
				null,   // user: null (사용자 정보 없음)
				null,   // cartItem: null (장바구니 아이템 정보 없음)
				cartItemQuantityRequestDto // 업데이트할 장바구니 아이템 정보
			))
			.isInstanceOf(BadRequestException.class);
	}

	@Test
	@DisplayName("요청된 개수가 재고보다 적은 경우에는 예외가 발생하지 않는다")
	void updateCartItemQuantity2() {
		// given
		Item item = new Item(
			null,         // store: null (스토어 정보 없음)
			"itemName",   // itemName: "itemName" (상품명)
			"test",       // explanation: "test" (설명)
			100,          // price: 100 (상품 가격)
			11            // remainingQuantity: 11 (재고 개수)
		);
		CartItem cartItem = new CartItem(
			null,  // user: null (사용자 정보 없음)
			item,  // item: 위에서 생성한 Item 객체
			10     // quantity: 10 (현재 장바구니에 담긴 수량)
		);

		Mockito.when(cartItemRepository.findByIdAndUserId(Mockito.any(), Mockito.any()))
			.thenReturn(Optional.of(cartItem));

		UpdateCartItemQuantityRequestDto cartItemQuantityRequestDto = new UpdateCartItemQuantityRequestDto(1);

		// when, then
		Assertions.assertThatCode(() -> cartItemService.updateCartItemQuantity(
				null,   // user: null (사용자 정보 없음)
				null,   // cartItem: null (장바구니 아이템 정보 없음)
				cartItemQuantityRequestDto // 업데이트할 장바구니 아이템 정보
			))
			.doesNotThrowAnyException();
	}
}
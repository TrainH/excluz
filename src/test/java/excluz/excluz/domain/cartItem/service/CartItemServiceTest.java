package excluz.excluz.domain.cartItem.service;

import static org.mockito.Mockito.*;

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
import excluz.excluz.common.entity.User;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.domain.cartItem.dto.request.CreateCartItemRequestDto;
import excluz.excluz.domain.cartItem.dto.request.UpdateCartItemQuantityRequestDto;
import excluz.excluz.domain.cartItem.dto.response.CreateCartItemResponseDto;
import excluz.excluz.domain.cartItem.repository.CartItemRepository;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

	// 가짜 객체
	@Mock
	private CartItemRepository cartItemRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ItemRepository itemRepository;

	// 실제 객체
	@InjectMocks
	private CartItemService cartItemService;

	@Test
	@DisplayName("success: 장바구니에 아이템 추가 메서드")
	void addItemToCart_success() {
		// given
		User user = mock(User.class);
		Item item = new Item(
			null,         // store: null (스토어 정보 없음)
			"itemName",   // itemName: "itemName" (상품명)
			"test",       // explanation: "test" (설명)
			100,          // price: 100 (상품 가격)
			100            // remainingQuantity: 100 (재고 개수)
		);

		Integer userId = 1;
		CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
			1, // itemId: 1 (아이템 아이디)
			10 // quantity: 10 (장바구니에 담는 물건 개수)
		);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(itemRepository.findById(requestDto.getItemId())).thenReturn(Optional.of(item));

		CartItem cartItem = new CartItem(
			user,
			item,
			requestDto.getQuantity()
		);

		when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

		// when
		CreateCartItemResponseDto result = cartItemService.addItemToCart(userId, requestDto);

		// than
		Assertions.assertThat(result).isNotNull(); // 반환값이 null이 아닌지 확인
		Assertions.assertThat(result.getMessage()).isEqualTo("장바구니에 굿즈가 담겼습니다."); // 응답 메시지 동일하게 나오는지 확인
		verify(cartItemRepository, times(1)).save(any(CartItem.class));  // save()가 1번만 실행되었는지 확인
	}

	@Test
	@DisplayName("success: 요청 개수 < 재고")
	void updateCartItemQuantity_success() {
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

	@Test
	@DisplayName("fail: 요청 개수 > 재고 (예외 발생)")
	void updateCartItemQuantity_fail() {
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
}
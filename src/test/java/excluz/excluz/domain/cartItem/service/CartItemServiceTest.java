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
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
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
			100           // remainingQuantity: 100 (재고 개수)
		);

		Integer userId = 1;
		CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
			1, // itemId: 1 (아이템 아이디)
			99 // quantity: 99 (장바구니에 담는 물건 개수)
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
		verify(cartItemRepository, times(1)).save(any(CartItem.class));  // save()가 1번만 실행되었는지 확인
		Assertions.assertThat(result).isNotNull(); // 반환값이 null이 아닌지 확인
		Assertions.assertThat(result.getMessage()).isEqualTo("장바구니에 굿즈가 담겼습니다."); // 응답 메시지 동일하게 나오는지 확인
	}

	@Test
	@DisplayName("success: 특정 아이템 재고와 동일한 수량 요청")
	void addItemToCart_success_matchingStockQuantity_case_1() {
		// given
		User user = mock(User.class);
		Item item = new Item(
			null,         // store: null (스토어 정보 없음)
			"itemName",   // itemName: "itemName" (상품명)
			"test",       // explanation: "test" (설명)
			100,          // price: 100 (상품 가격)
			100           // remainingQuantity: 100 (재고 개수)
		);

		Integer userId = 1;
		CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
			1,  // itemId: 1 (아이템 아이디)
			100 // quantity: 100 (장바구니에 담는 물건 개수)
		);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(itemRepository.findById(requestDto.getItemId())).thenReturn(Optional.of(item));
		when(cartItemRepository.findByUserIdAndItemId(userId, requestDto.getItemId()))
			.thenReturn(Optional.of(new CartItem(user, item, 0))); // 기존 장바구니에 0개 있음
		when(cartItemRepository.save(any(CartItem.class))).thenReturn(mock(CartItem.class));

		// when
		CreateCartItemResponseDto result = cartItemService.addItemToCart(userId, requestDto);

		// then
		verify(cartItemRepository, times(1)).save(any(CartItem.class));  // save()가 1번만 실행되었는지 확인
		Assertions.assertThat(result).isNotNull(); // 반환값이 null이 아닌지 확인
		Assertions.assertThat(result.getMessage()).isEqualTo("장바구니에 굿즈가 담겼습니다."); // 응답 메시지 동일하게 나오는지 확인
	}

	@Test
	@DisplayName("success: 기 요청 개수 + 새로운 요청 개수 = 특정 아이템 재고 개수")
	void addItemToCart_success_matchingStockQuantity_case_2() {
		// given
		User user = mock(User.class);
		Item item = new Item(
			null,         // store: null (스토어 정보 없음)
			"itemName",   // itemName: "itemName" (상품명)
			"test",       // explanation: "test" (설명)
			100,          // price: 100 (상품 가격)
			100           // remainingQuantity: 100 (재고 개수)
		);

		Integer userId = 1;
		CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
			1, // itemId: 1 (아이템 아이디)
			50 // quantity: 50 (장바구니에 담는 물건 개수)
		);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(itemRepository.findById(requestDto.getItemId())).thenReturn(Optional.of(item));
		when(cartItemRepository.findByUserIdAndItemId(userId, requestDto.getItemId()))
			.thenReturn(Optional.of(new CartItem(user, item, 50))); // 기존 장바구니에 50개 있음
		when(cartItemRepository.save(any(CartItem.class))).thenReturn(mock(CartItem.class));

		// when
		CreateCartItemResponseDto result = cartItemService.addItemToCart(userId, requestDto);

		// then
		verify(cartItemRepository, times(1)).save(any(CartItem.class));
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.getMessage()).isEqualTo("장바구니에 굿즈가 담겼습니다.");
	}

	@Test
	@DisplayName("fail: 물품 추가 시 존재하지 않는 유저 (예외 발생)")
	void addItemToCart_fail_userNotFound() {
		// given
		Integer userId = 1;
		CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
			1, // itemId: 1 (아이템 아이디)
			10 // quantity: 10 (장바구니에 담는 물건 개수)
		);

		when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class))); // 유저는 존재
		when(itemRepository.findById(requestDto.getItemId())).thenReturn(Optional.empty()); // 아이템이 존재하지 않음

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.addItemToCart(userId, requestDto))
			.isInstanceOf(NotFoundException.class);
	}

	@Test
	@DisplayName("fail: 물품 추가 시 존재하지 않는 아이템 (예외 발생)")
	void addItemToCart_fail_itemNotFound() {
		// given
		Integer userId = 1;
		CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
			1, // itemId: 1 (아이템 아이디)
			10 // quantity: 10 (장바구니에 담는 물건 개수)
		);

		when(userRepository.findById(userId)).thenReturn(Optional.empty()); // 아이템이 없는 경우

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.addItemToCart(userId, requestDto))
			.isInstanceOf(NotFoundException.class);
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
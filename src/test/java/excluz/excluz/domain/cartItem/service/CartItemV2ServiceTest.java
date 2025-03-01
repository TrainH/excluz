package excluz.excluz.domain.cartItem.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import excluz.excluz.common.entity.CartItem;
import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.User;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.domain.cartItem.dto.response.CartItemListResponseDto;
import excluz.excluz.domain.cartItem.repository.CartItemV2Repository;
import excluz.excluz.domain.user.enums.UserRole;

@ExtendWith(MockitoExtension.class)
class CartItemV2ServiceTest {
	// 가짜 객체
	@Mock
	private CartItemV2Repository cartItemV2Repository;

	// 실제 객체
	@InjectMocks
	private CartItemV2Service cartItemV2Service;

	/*
	 * getCartItemListV1
	 */
	@Test
	@DisplayName("success: v2-1 장바구니 아이템 목록 조회")
	void getCartItemListV1() {
		// given
		User user = mock(User.class);
		Store store = mock(Store.class);

		Item item1 = new Item(
			store,         // store: 위에서 생성한 store 객체
			"itemName",   // itemName: "itemName" (상품명)
			"test",       // explanation: "test" (설명)
			100,          // price: 100 (상품 가격)
			10            // remainingQuantity: 10 (재고 개수)
		);
		Item item2 = new Item(
			store,         // store: 위에서 생성한 store 객체
			"itemName2",   // itemName: "itemName2" (상품명)
			"test2",       // explanation: "test2" (설명)
			200,          // price: 200 (상품 가격)
			20            // remainingQuantity: 20 (재고 개수)
		);

		CartItem cartItem1 = new CartItem(
			user,  // user: 위에서 생성한 user 객체
			item1,  // item: 위에서 생성한 Item1 객체
			2      // quantity: 2 (현재 장바구니에 담긴 수량)
		);
		CartItem cartItem2 = new CartItem(
			user,  // user: 위에서 생성한 user 객체
			item2,  // item: 위에서 생성한 Item2 객체
			3      // quantity: 3 (현재 장바구니에 담긴 수량)
		);

		int page = 0; // 페이지 번호
		int size = 10; // 페이지당 아이템 개수
		Pageable pageable = PageRequest.of(page, size);

		Page<CartItem> cartItemPage = new PageImpl<>(List.of(cartItem1, cartItem2), pageable, 2);

		ReflectionTestUtils.setField(user, "id", 1);
		ReflectionTestUtils.setField(cartItem1, "id", 1);
		ReflectionTestUtils.setField(cartItem2, "id", 2);

		UserRole userRole = UserRole.CUSTOMER;

		when(cartItemV2Repository.findByUserIdV1(eq(user.getId()), any(Pageable.class)))
			.thenReturn(cartItemPage);

		// when
		CartItemListResponseDto result = cartItemV2Service.getCartItemListV1(user.getId(), userRole, page, size);

		// then
		verify(cartItemV2Repository, times(1)).findByUserIdV1(eq(user.getId()), any(Pageable.class)); // findByUserIdV1()가 1번 호출되었는지 확인

		Assertions.assertThat(result).isNotNull(); // 결과가 null이 아닌지 확인
		Assertions.assertThat(result.getCartItemList()).hasSize(2); // 장바구니에 2개 아이템 있는지 확인
		Assertions.assertThat(result.getTotalPrice()).isEqualTo(800); // (100*2 + 200*3) = 800 확인
		Assertions.assertThat(result.getCartItemList().getContent().get(0).getQuantity()).isEqualTo(2); // 첫 번째 아이템 개수 확인
		Assertions.assertThat(result.getCartItemList().getContent().get(1).getQuantity()).isEqualTo(3); // 두 번째 아이템 개수 확인
	}

	@Test
	@DisplayName("success: 장바구니가 비어있는 경우")
	void getCartItemListV1EmptyCart() {
		// given
		Integer userId = 1;
		UserRole userRole = UserRole.CUSTOMER;

		int page = 0; // 페이지 번호
		int size = 10; // 페이지당 아이템 개수

		Page<CartItem> emptyPage = Page.empty();

		when(cartItemV2Repository.findByUserIdV1(eq(userId), any(Pageable.class)))
			.thenReturn(emptyPage); // 빈 페이지 반환

		// when
		CartItemListResponseDto result = cartItemV2Service.getCartItemListV1(userId, userRole, page, size);

		// then
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.getCartItemList()).isEmpty(); // 빈 리스트인지 확인
		Assertions.assertThat(result.getTotalPrice()).isEqualTo(0); // 총 가격이 0인지 확인

		verify(cartItemV2Repository, times(1)).findByUserIdV1(eq(userId), any(Pageable.class)); // findByUserIdV1()가 1번 호출되었는지 확인
	}

	@Test
	@DisplayName("fail: 유효하지 않은 유저 역할 (예외 발생)")
	void getCartItemListV1InvalidUserRole() {
		// given
		Integer userId = 1;
		UserRole userRole = UserRole.STREAMER; // CUSTOMER가 아닌 경우

		int page = 0; // 페이지 번호
		int size = 10; // 페이지당 아이템 개수

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemV2Service.getCartItemListV1(userId, userRole, page, size))
			.isInstanceOf(ForbiddenException.class); // ForbiddenException 예외 발생
	}


	/*
	 * getCartItemListV2
	 */
	@Test
	@DisplayName("success: v2-2 장바구니 아이템 목록 조회")
	void getCartItemListV2() {
		// given
		User user = mock(User.class);
		Store store = mock(Store.class);

		Item item1 = new Item(
			store,         // store: 위에서 생성한 store 객체
			"itemName",   // itemName: "itemName" (상품명)
			"test",       // explanation: "test" (설명)
			100,          // price: 100 (상품 가격)
			10            // remainingQuantity: 10 (재고 개수)
		);
		Item item2 = new Item(
			store,         // store: 위에서 생성한 store 객체
			"itemName2",   // itemName: "itemName2" (상품명)
			"test2",       // explanation: "test2" (설명)
			200,          // price: 200 (상품 가격)
			20            // remainingQuantity: 20 (재고 개수)
		);

		CartItem cartItem1 = new CartItem(
			user,  // user: 위에서 생성한 user 객체
			item1,  // item: 위에서 생성한 Item1 객체
			2      // quantity: 2 (현재 장바구니에 담긴 수량)
		);
		CartItem cartItem2 = new CartItem(
			user,  // user: 위에서 생성한 user 객체
			item2,  // item: 위에서 생성한 Item2 객체
			3      // quantity: 3 (현재 장바구니에 담긴 수량)
		);

		int page = 0; // 페이지 번호
		int size = 10; // 페이지당 아이템 개수
		Pageable pageable = PageRequest.of(page, size);

		Page<CartItem> cartItemPage = new PageImpl<>(List.of(cartItem1, cartItem2), pageable, 2);

		ReflectionTestUtils.setField(user, "id", 1);
		ReflectionTestUtils.setField(cartItem1, "id", 1);
		ReflectionTestUtils.setField(cartItem2, "id", 2);

		UserRole userRole = UserRole.CUSTOMER;

		when(cartItemV2Repository.findByUserIdV2(eq(user.getId()), any(Pageable.class)))
			.thenReturn(cartItemPage);

		// when
		CartItemListResponseDto result = cartItemV2Service.getCartItemListV2(user.getId(), userRole, page, size);

		// then
		verify(cartItemV2Repository, times(1)).findByUserIdV2(eq(user.getId()), any(Pageable.class)); // findByUserIdV2()가 1번 호출되었는지 확인

		Assertions.assertThat(result).isNotNull(); // 결과가 null이 아닌지 확인
		Assertions.assertThat(result.getCartItemList()).hasSize(2); // 장바구니에 2개 아이템 있는지 확인
		Assertions.assertThat(result.getTotalPrice()).isEqualTo(800); // (100*2 + 200*3) = 800 확인
		Assertions.assertThat(result.getCartItemList().getContent().get(0).getQuantity()).isEqualTo(2); // 첫 번째 아이템 개수 확인
		Assertions.assertThat(result.getCartItemList().getContent().get(1).getQuantity()).isEqualTo(3); // 두 번째 아이템 개수 확인
	}

	@Test
	@DisplayName("success: 장바구니가 비어있는 경우")
	void getCartItemListV2EmptyCart() {
		// given
		Integer userId = 1;
		UserRole userRole = UserRole.CUSTOMER;

		int page = 0; // 페이지 번호
		int size = 10; // 페이지당 아이템 개수

		Page<CartItem> emptyPage = Page.empty();

		when(cartItemV2Repository.findByUserIdV2(eq(userId), any(Pageable.class)))
			.thenReturn(emptyPage); // 빈 페이지 반환

		// when
		CartItemListResponseDto result = cartItemV2Service.getCartItemListV2(userId, userRole, page, size);

		// then
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.getCartItemList()).isEmpty(); // 빈 리스트인지 확인
		Assertions.assertThat(result.getTotalPrice()).isEqualTo(0); // 총 가격이 0인지 확인

		verify(cartItemV2Repository, times(1)).findByUserIdV2(eq(userId), any(Pageable.class)); // findByUserIdV2()가 1번 호출되었는지 확인
	}

	@Test
	@DisplayName("fail: 유효하지 않은 유저 역할 (예외 발생)")
	void getCartItemListV2InvalidUserRole() {
		// given
		Integer userId = 1;
		UserRole userRole = UserRole.STREAMER; // CUSTOMER가 아닌 경우

		int page = 0; // 페이지 번호
		int size = 10; // 페이지당 아이템 개수

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemV2Service.getCartItemListV2(userId, userRole, page, size))
			.isInstanceOf(ForbiddenException.class); // ForbiddenException 예외 발생
	}
}
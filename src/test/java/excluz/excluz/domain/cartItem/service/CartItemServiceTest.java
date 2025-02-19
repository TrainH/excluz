package excluz.excluz.domain.cartItem.service;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import excluz.excluz.common.entity.CartItem;
import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.common.entity.User;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.domain.cartItem.dto.request.CreateCartItemRequestDto;
import excluz.excluz.domain.cartItem.dto.request.UpdateCartItemQuantityRequestDto;
import excluz.excluz.domain.cartItem.dto.response.CartItemListResponseDto;
import excluz.excluz.domain.cartItem.dto.response.CreateCartItemResponseDto;
import excluz.excluz.domain.cartItem.dto.response.GetCartItemResponseDto;
import excluz.excluz.domain.cartItem.repository.CartItemRepository;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.user.enums.UserRole;
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

	/*
	 * addItemToCart
	 */
	@Test
	@DisplayName("success: 장바구니 아이템 추가")
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
		UserRole userRole = UserRole.CUSTOMER;

		CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
			1, // itemId: 1 (아이템 아이디)
			99 // quantity: 99 (장바구니에 담는 물건 개수)
		);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(itemRepository.findById(requestDto.getItemId())).thenReturn(Optional.of(item));
		when(cartItemRepository.findByUserIdAndItemId(userId, requestDto.getItemId()))
			.thenReturn(Optional.of(new CartItem(user, item, 0)));

		CartItem cartItem = new CartItem(
			user,
			item,
			requestDto.getQuantity()
		);

		when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

		// when
		CreateCartItemResponseDto result = cartItemService.addItemToCart(userId, userRole, requestDto);

		// than
		verify(cartItemRepository, times(1)).save(any(CartItem.class)); // save()가 1번만 실행되었는지 확인
		Assertions.assertThat(result).isNotNull(); // 반환값이 null이 아닌지 확인
		Assertions.assertThat(result.getQuantity()).isEqualTo(99); // 요청 수량이 맞는지 확인
		Assertions.assertThat(result.getItemPrice()).isEqualTo(100); // 아이템 금액 맞는지 확인
		Assertions.assertThat(result.getTotalItemPrice()).isEqualTo(9900); // 아이템 총액이 맞는지 확인
	}

	@Test
	@DisplayName("success: 요청 수량 = 재고")
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
		UserRole userRole = UserRole.CUSTOMER;

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
		CreateCartItemResponseDto result = cartItemService.addItemToCart(userId, userRole, requestDto);

		// then
		verify(cartItemRepository, times(1)).save(any(CartItem.class));  // save()가 1번만 실행되었는지 확인
		Assertions.assertThat(result).isNotNull(); // 반환값이 null이 아닌지 확인
		Assertions.assertThat(result.getQuantity()).isEqualTo(100); // 요청 수량이 맞는지 확인
		Assertions.assertThat(result.getItemPrice()).isEqualTo(100); // 아이템 금액 맞는지 확인
		Assertions.assertThat(result.getTotalItemPrice()).isEqualTo(10000); // 아이템 총액이 맞는지 확인
	}

	@Test
	@DisplayName("success: 장바구니에 있는 수량 + 추가 요청 수량 = 재고")
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
		UserRole userRole = UserRole.CUSTOMER;

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
		CreateCartItemResponseDto result = cartItemService.addItemToCart(userId, userRole, requestDto);

		// then
		verify(cartItemRepository, times(1)).save(any(CartItem.class));  // save()가 1번만 실행되었는지 확인
		Assertions.assertThat(result).isNotNull(); // 반환값이 null이 아닌지 확인
		Assertions.assertThat(result.getQuantity()).isEqualTo(100); // 요청 수량이 맞는지 확인
		Assertions.assertThat(result.getItemPrice()).isEqualTo(100); // 아이템 금액 맞는지 확인
		Assertions.assertThat(result.getTotalItemPrice()).isEqualTo(10000); // 아이템 총액이 맞는지 확인
	}

	@Test
	@DisplayName("fail: 물품 추가 시 존재하지 않는 유저 (예외 발생)")
	void addItemToCart_fail_userNotFound() {
		// given
		Integer userId = 1;
		UserRole userRole = UserRole.CUSTOMER;
		CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
			1, // itemId: 1 (아이템 아이디)
			10 // quantity: 10 (장바구니에 담는 물건 개수)
		);

		when(userRepository.findById(userId)).thenReturn(Optional.empty()); // 유저가 없는 경우

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.addItemToCart(userId, userRole, requestDto))
			.isInstanceOf(NotFoundException.class); // NotFoundException 예외 발생
	}

	@Test
	@DisplayName("fail: 물품 추가 시 존재하지 않는 아이템 (예외 발생)")
	void addItemToCart_fail_itemNotFound() {
		// given
		Integer userId = 1;
		UserRole userRole = UserRole.CUSTOMER;
		CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
			1, // itemId: 1 (아이템 아이디)
			10 // quantity: 10 (장바구니에 담는 물건 개수)
		);

		when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class))); // 유저는 존재
		when(itemRepository.findById(requestDto.getItemId())).thenReturn(Optional.empty()); // 아이템이 존재하지 않음

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.addItemToCart(userId, userRole, requestDto))
			.isInstanceOf(NotFoundException.class); // NotFoundException 예외 발생
	}

	@Test
	@DisplayName("fail: 요청 개수 > 재고 (예외 발생)")
	void addItemToCart_fail_stockExceeded_case_1() {
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
		UserRole userRole = UserRole.CUSTOMER;

		CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
			1,  // itemId: 1 (아이템 아이디)
			101 // quantity: 101 (장바구니에 담는 물건 개수)
		);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(itemRepository.findById(requestDto.getItemId())).thenReturn(Optional.of(item));
		when(cartItemRepository.findByUserIdAndItemId(userId, requestDto.getItemId()))
			.thenReturn(Optional.of(new CartItem(user, item, 0))); // 기존 장바구니에 0개 있음

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.addItemToCart(userId, userRole, requestDto))
			.isInstanceOf(BadRequestException.class); // BadRequestException 예외 발생
	}

	@Test
	@DisplayName("fail: 기 요청 개수 + 새로운 요청 개수 > 재고 (예외 발생)")
	void addItemToCart_fail_stockExceeded_case_2() {
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
		UserRole userRole = UserRole.CUSTOMER;

		CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
			1, // itemId: 1 (아이템 아이디)
			51 // quantity: 51 (장바구니에 담는 물건 개수)
		);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(itemRepository.findById(requestDto.getItemId())).thenReturn(Optional.of(item));
		when(cartItemRepository.findByUserIdAndItemId(userId, requestDto.getItemId()))
			.thenReturn(Optional.of(new CartItem(user, item, 50))); // 기존 장바구니에 50개 있음

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.addItemToCart(userId, userRole, requestDto))
			.isInstanceOf(BadRequestException.class); // BadRequestException 예외 발생
	}

	@Test
	@DisplayName("fail: 유효하지 않은 유저 역할 (예외 발생)")
	void addItemToCart_fail_invalidUserRole() {
		// given
		Integer userId = 1;
		UserRole userRole = UserRole.STREAMER; // CUSTOMER가 아닌 경우
		CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
			1, // itemId: 1 (아이템 아이디)
			1 // quantity: 1 (장바구니에 담는 물건 개수)
		);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.addItemToCart(userId, userRole, requestDto))
			.isInstanceOf(ForbiddenException.class); // ForbiddenException 예외 발생
	}


	/*
	 * getCartItem
	 */
	@Test
	@DisplayName("success: 장바구니 아이템 단건 조회")
	void getCartItem_success() {
		// given
		User user = mock(User.class);
		Store store = mock(Store.class);
		Item item = new Item(
			store,         // store: 위에서 생성한 store 객체
			"itemName",   // itemName: "itemName" (상품명)
			"test",       // explanation: "test" (설명)
			100,          // price: 100 (상품 가격)
			10            // remainingQuantity: 10 (재고 개수)
		);
		CartItem cartItem = new CartItem(
			user,  // user: 위에서 생성한 user 객체
			item,  // item: 위에서 생성한 Item 객체
			10     // quantity: 10 (현재 장바구니에 담긴 수량)
		);

		ReflectionTestUtils.setField(user, "id", 1); // user id 값을 1로 세팅
		ReflectionTestUtils.setField(cartItem, "id", 1); // cartItem id 값을 1로 세팅

		UserRole userRole = UserRole.CUSTOMER;

		when(cartItemRepository.findByIdAndUserId(cartItem.getId(), user.getId()))
			.thenReturn(Optional.of(cartItem));

		// when
		GetCartItemResponseDto result = cartItemService.getCartItem(user.getId(), userRole, cartItem.getId());

		// then
		verify(cartItemRepository, times(1)).findByIdAndUserId(cartItem.getId(), user.getId()); // findByIdAndUserId()가 1번 호출되었는지 확인

		Assertions.assertThat(result).isNotNull(); // 반환된 응답 객체가 null이 아닌지 확인
		Assertions.assertThat(result.getCartItemId()).isEqualTo(cartItem.getId()); // 요청한 cartItemId와 동일한지 확인
		Assertions.assertThat(result.getQuantity()).isEqualTo(10); // 장바구니에 담긴 개수가 요청한 수량과 일치하는지 확인
		Assertions.assertThat(result.getItemPrice()).isEqualTo(100); // 아이템의 단가가 예상 값과 일치하는지 확인
		Assertions.assertThat(result.getTotalItemPrice()).isEqualTo(1000); // 총 가격(단가 * 개수)이 올바르게 계산되었는지 확인
	}

	@Test
	@DisplayName("fail: 존재하지 않는 장바구니 아이템 ID (예외 발생)")
	void getCartItem_fail_notFound() {
		// given
		User user = mock(User.class);
		ReflectionTestUtils.setField(user, "id", 1); // user id 값을 1로 세팅
		UserRole userRole = UserRole.CUSTOMER;
		Integer cartItemId = 999; // 존재하지 않는 ID

		when(cartItemRepository.findByIdAndUserId(cartItemId, user.getId()))
			.thenReturn(Optional.empty()); // 없는 데이터

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.getCartItem(user.getId(), userRole, cartItemId))
			.isInstanceOf(NotFoundException.class); // NotFoundException 예외 발생
	}

	@Test
	@DisplayName("fail: 유효하지 않은 유저 역할 (예외 발생)")
	void getCartItem_fail_invalidUserRole() {
		// given
		Integer userId = 1;
		Integer cartItemId = 1;
		UserRole userRole = UserRole.STREAMER; // 유효하지 않은 유저 역할 (CUSTOMER가 아님)

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.getCartItem(userId, userRole, cartItemId))
			.isInstanceOf(ForbiddenException.class); // ForbiddenException 예외 발생
	}

	@Test
	@DisplayName("fail: 다른 유저의 장바구니 아이템 조회 시 예외 발생")
	void getCartItem_fail_wrongUser() {
		// given
		User userA = User.builder()
			.name("유저A")
			.nickName("userA")
			.phoneNumber("010-1111-1111")
			.address("서울시 강남구")
			.email("userA@example.com")
			.password("passwordA!!!!!!!!!")
			.build();

		User userB = User.builder()
			.name("유저B")
			.nickName("userB")
			.phoneNumber("010-2222-2222")
			.address("서울시 서초구")
			.email("userB@example.com")
			.password("passwordB!!!!!!!!!")
			.build();

		ReflectionTestUtils.setField(userA, "id", 1);
		ReflectionTestUtils.setField(userB, "id", 2);

		Streamer streamer = new Streamer(); // Streamer 객체
		ReflectionTestUtils.setField(streamer, "id", 1); // Streamer의 ID 설정

		Store store = Store.builder()
			.streamer(streamer) // streamer: 위에서 생성한 streamer 객체
			.address("서울시 강남구") // 스토어 주소
			.storeName("테스트 스토어") // 스토어 이름
			.registrationNumber("123-45-67890") // 사업자 등록번호
			.build();

		Item item = new Item(
			store,         // store: 위에서 생성한 store 객체
			"itemName",   // itemName: "itemName" (상품명)
			"test",       // explanation: "test" (설명)
			100,          // price: 100 (상품 가격)
			10            // remainingQuantity: 10 (재고 개수)
		);

		CartItem cartItem = new CartItem(userB, item, 5); // userB의 장바구니 아이템
		ReflectionTestUtils.setField(cartItem, "id", 1); // cartItemId 설정

		UserRole userRole = UserRole.CUSTOMER;

		when(cartItemRepository.findByIdAndUserId(1, 1))
			.thenReturn(Optional.empty()); // userA의 ID로는 userB의 장바구니 아이템 조회 불가능

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.getCartItem(userA.getId(), userRole, cartItem.getId()))
			.isInstanceOf(NotFoundException.class); // NotFoundException 예외 발생
	}


	/*
	 * getCartItemList
	 */
	@Test
	@DisplayName("success: 장바구니 아이템 목록 조회")
	void getCartItemList_success() {
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

		ReflectionTestUtils.setField(user, "id", 1);
		ReflectionTestUtils.setField(cartItem1, "id", 1);
		ReflectionTestUtils.setField(cartItem2, "id", 2);

		UserRole userRole = UserRole.CUSTOMER;

		when(cartItemRepository.findByUserId(user.getId()))
			.thenReturn(List.of(cartItem1, cartItem2));

		// when
		CartItemListResponseDto result = cartItemService.getCartItemList(user.getId(), userRole);

		// then
		Assertions.assertThat(result).isNotNull(); // 결과가 null이 아닌지 확인
		Assertions.assertThat(result.getCartItemList()).hasSize(2); // 장바구니에 2개 아이템 있는지 확인
		Assertions.assertThat(result.getTotalPrice()).isEqualTo(800); // (100*2 + 200*3) = 800 확인
		Assertions.assertThat(result.getCartItemList().get(0).getQuantity()).isEqualTo(2); // 첫 번째 아이템 개수 확인
		Assertions.assertThat(result.getCartItemList().get(1).getQuantity()).isEqualTo(3); // 두 번째 아이템 개수 확인
	}

	@Test
	@DisplayName("success: 장바구니가 비어있는 경우")
	void getCartItemList_success_emptyCart() {
		// given
		Integer userId = 1;
		UserRole userRole = UserRole.CUSTOMER;

		when(cartItemRepository.findByUserId(userId))
			.thenReturn(Collections.emptyList()); // 빈 리스트 반환

		// when
		CartItemListResponseDto result = cartItemService.getCartItemList(userId, userRole);

		// then
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.getCartItemList()).isEmpty(); // 빈 리스트인지 확인
		Assertions.assertThat(result.getTotalPrice()).isEqualTo(0); // 총 가격이 0인지 확인
	}

	@Test
	@DisplayName("fail: 유효하지 않은 유저 역할 (예외 발생)")
	void getCartItemList_fail_invalidUserRole() {
		// given
		Integer userId = 1;
		UserRole userRole = UserRole.STREAMER; // CUSTOMER가 아닌 경우

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.getCartItemList(userId, userRole))
			.isInstanceOf(ForbiddenException.class); // ForbiddenException 예외 발생
	}



	/*
	 * updateCartItemQuantity
	 */
	@Test
	@DisplayName("success: 요청 개수 < 재고")
	void updateCartItemQuantity_success() {
		// given
		User user = mock(User.class);
		Item item = new Item(
			null,         // store: null (스토어 정보 없음)
			"itemName",   // itemName: "itemName" (상품명)
			"test",       // explanation: "test" (설명)
			100,          // price: 100 (상품 가격)
			11            // remainingQuantity: 11 (재고 개수)
		);
		CartItem cartItem = new CartItem(
			user,  // user: 위에서 생성한 user 객체
			item,  // item: 위에서 생성한 Item 객체
			10     // quantity: 10 (현재 장바구니에 담긴 수량)
		);

		UserRole userRole = UserRole.CUSTOMER;
		ReflectionTestUtils.setField(user, "id", 1);
		ReflectionTestUtils.setField(cartItem, "id", 1);

		when(cartItemRepository.findByIdAndUserId(cartItem.getId(), user.getId()))
			.thenReturn(Optional.of(cartItem));

		UpdateCartItemQuantityRequestDto requestDto = new UpdateCartItemQuantityRequestDto(1); // 개수 수정

		// when, then
		Assertions.assertThatCode(() -> cartItemService.updateCartItemQuantity(
				user.getId(),
				userRole,
				cartItem.getId(),
				requestDto // 업데이트할 장바구니 아이템 정보
			))
			.doesNotThrowAnyException(); // 예외 발생하지 않아야 함
		verify(cartItemRepository, times(1)).save(any(CartItem.class));  // save()가 1번만 실행되었는지 확인
	}

	@Test
	@DisplayName("fail: 요청 개수 > 재고 (예외 발생)")
	void updateCartItemQuantity_fail() {
		// given
		User user = mock(User.class);
		Item item = new Item(
			null,         // store: null (스토어 정보 없음)
			"itemName",   // itemName: "itemName" (상품명)
			"test",       // explanation: "test" (설명)
			100,          // price: 100 (상품 가격)
			10            // remainingQuantity: 10 (재고 개수)
		);
		CartItem cartItem = new CartItem(
			user,  // user: 위에서 생성한 user 객체
			item,  // item: 위에서 생성한 Item 객체
			10     // quantity: 10 (현재 장바구니에 담긴 수량)
		);

		UserRole userRole = UserRole.CUSTOMER;
		ReflectionTestUtils.setField(user, "id", 1);
		ReflectionTestUtils.setField(cartItem, "id", 1);

		when(cartItemRepository.findByIdAndUserId(cartItem.getId(), user.getId()))
			.thenReturn(Optional.of(cartItem));

		UpdateCartItemQuantityRequestDto requestDto = new UpdateCartItemQuantityRequestDto(100); // 100개로 개수 수정

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.updateCartItemQuantity(
				user.getId(),
				userRole,
				cartItem.getId(),
				requestDto // 업데이트할 장바구니 아이템 정보
			))
			.isInstanceOf(BadRequestException.class); // BadRequestException 예외 발생
	}

	@Test
	@DisplayName("fail: 존재하지 않는 장바구니 아이템 (예외 발생)")
	void updateCartItemQuantity_fail_cartItemNotFound() {
		// given
		Integer userId = 1;
		UserRole userRole = UserRole.CUSTOMER;
		Integer cartItemId = 999; // 존재하지 않는 ID

		when(cartItemRepository.findByIdAndUserId(cartItemId, userId))
			.thenReturn(Optional.empty()); // 해당 장바구니 아이템 없음

		UpdateCartItemQuantityRequestDto requestDto = new UpdateCartItemQuantityRequestDto(5); // 5개로 변경 요청

		// when, then
		Assertions.assertThatThrownBy(() -> cartItemService.updateCartItemQuantity(
				userId,
				userRole,
				cartItemId,
				requestDto
			))
			.isInstanceOf(NotFoundException.class); // NotFoundException 예외 발생
	}


	/*
	 * removeCartItem
	 */
	@Test
	@DisplayName("success: 장바구니 아이템 삭제")
	void removeCartItem_success() {
		// given
		User user = mock(User.class);
		Store store = mock(Store.class);
		Item item = new Item(
			store,         // store: 위에서 생성한 store 객체
			"itemName",   // itemName: "itemName" (상품명)
			"test",       // explanation: "test" (설명)
			100,          // price: 100 (상품 가격)
			10            // remainingQuantity: 10 (재고 개수)
		);
		CartItem cartItem = new CartItem(
			user,  // user: 위에서 생성한 user 객체
			item,  // item: 위에서 생성한 Item 객체
			10     // quantity: 10 (현재 장바구니에 담긴 수량)
		);

		ReflectionTestUtils.setField(user, "id", 1);
		ReflectionTestUtils.setField(cartItem, "id", 1);

		Integer userId = user.getId();
		UserRole userRole = UserRole.CUSTOMER;
		Integer cartItemId = cartItem.getId();

		when(cartItemRepository.findByIdAndUserId(cartItemId, userId))
			.thenReturn(Optional.of(cartItem)); // 장바구니 아이템 찾기 성공

		// when
		cartItemService.removeCartItem(userId, userRole, cartItemId);

		// then
		verify(cartItemRepository, times(1)).delete(cartItem); // delete() 메서드가 1번 호출되었는지 확인
	}
}
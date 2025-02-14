package excluz.excluz.domain.cartItem.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import excluz.excluz.common.entity.CartItem;
import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.User;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.cartItem.dto.request.CreateCartItemRequestDto;
import excluz.excluz.domain.cartItem.dto.request.UpdateCartItemQuantityRequestDto;
import excluz.excluz.domain.cartItem.dto.response.CartItemListResponseDto;
import excluz.excluz.domain.cartItem.dto.response.CreateCartItemResponseDto;
import excluz.excluz.domain.cartItem.dto.response.GetCartItemResponseDto;
import excluz.excluz.domain.cartItem.repository.CartItemRepository;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {
	private final CartItemRepository cartItemRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	// 물품 추가
	@Transactional
	public CreateCartItemResponseDto addItemToCart(Integer userId, CreateCartItemRequestDto requestDto) {
		// todo 추후 의논할 것: CartitemService에서 다른 엔티티의 Repository를 의존하는 구조가 좋은 설계인지 추후에 다같이 고민해보면 좋을 것 같습니다!
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
		Item item = itemRepository.findById(requestDto.getItemId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND));

		CartItem cartItem = new CartItem(user, item, requestDto.getQuantity());
		cartItemRepository.save(cartItem);

		return new CreateCartItemResponseDto();
	}

	// 물품 단건 조회
	public GetCartItemResponseDto getCartItem(Integer userId, Integer cartItemId) {
		CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND));

		return GetCartItemResponseDto.builder()
			.cartItemId(cartItem.getId())
			.quantity(cartItem.getQuantity())
			.itemPrice(cartItem.getItem().getPrice())
			.build();
	}

	// 물품 다건 조회
	public CartItemListResponseDto getCartItemList(Integer userId) {
		List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

		List<GetCartItemResponseDto> cartItemList = cartItems.stream()
			.map(item -> GetCartItemResponseDto.builder()
				.cartItemId(item.getId())
				.quantity(item.getQuantity())
				.itemPrice(item.getItem().getPrice())
				.build()
			)
			.collect(Collectors.toList());

		return new CartItemListResponseDto(cartItemList);
	}

	// 물품 개수 수정
	@Transactional
	public GetCartItemResponseDto updateCartItemQuantity(
		Integer userId,
		Integer cartItemId,
		UpdateCartItemQuantityRequestDto requestDto
	) {
		// 장바구니에서 해당 아이템 찾기
		CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.CART_ITEM_NOT_FOUND));

		// 장바구니에 담긴 상품의 정보 가져오기
		Item item = cartItem.getItem();

		// 재고 체크 (요청된 개수가 재고보다 많은 경우 예외 발생)
		if (requestDto.getQuantity() > item.getRemainingQuantity()) {
			throw new BadRequestException(ErrorCode.OUT_OF_STOCK);
		}

		// 개수 업데이트
		cartItem.updateQuantity(requestDto.getQuantity());

		return GetCartItemResponseDto.builder()
			.cartItemId(cartItem.getId())
			.quantity(cartItem.getQuantity())
			.itemPrice(cartItem.getItem().getPrice())
			.build();
	}

	// 물품 삭제(단건)
	@Transactional
	public void removeCartItem(Integer userId, Integer cartItemId) {
		CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND));

		cartItemRepository.delete(cartItem);
	}
}

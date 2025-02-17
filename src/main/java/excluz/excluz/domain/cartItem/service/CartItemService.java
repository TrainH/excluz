package excluz.excluz.domain.cartItem.service;

import java.util.List;
import java.util.Optional;
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
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
		Item item = itemRepository.findById(requestDto.getItemId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND));

		// 기존 장바구니에서 동일한 상품이 있는지 확인
		CartItem cartItem = cartItemRepository.findByUserIdAndItemId(userId, requestDto.getItemId())
			.orElseGet(() -> new CartItem(user, item, 0)); // 없으면 새 객체 생성 (초기 수량 0)

		// 총 수량 계산
		Integer newQuantity = cartItem.getQuantity() + requestDto.getQuantity();

		// 재고 초과 여부 확인
		if (newQuantity > item.getRemainingQuantity()) {
			throw new BadRequestException(ErrorCode.OUT_OF_STOCK);
		}

		// 수량 업데이트 및 저장
		cartItem.updateQuantity(newQuantity);
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

		// 재고 체크 (요청된 개수(기존 장바구니 개수 + 새로 요청한 개수)가 재고보다 많은 경우 예외 발생)
		if (cartItem.getQuantity() + requestDto.getQuantity() > item.getRemainingQuantity()) {
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
			.orElseThrow(() -> new NotFoundException(ErrorCode.CART_ITEM_NOT_FOUND));

		cartItemRepository.delete(cartItem);
	}
}

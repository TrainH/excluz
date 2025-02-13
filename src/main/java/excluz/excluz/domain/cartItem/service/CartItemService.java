package excluz.excluz.domain.cartItem.service;

import org.springframework.stereotype.Service;

import excluz.excluz.common.entity.CartItem;
import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.User;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.cartItem.dto.request.CreateCartItemRequestDto;
import excluz.excluz.domain.cartItem.dto.response.CreateCartItemResponseDto;
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

		CartItem cartItem = new CartItem(user, item, requestDto.getQuantity());
		cartItemRepository.save(cartItem);

		return new CreateCartItemResponseDto();
	}

}

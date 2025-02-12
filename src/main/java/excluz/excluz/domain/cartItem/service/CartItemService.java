package excluz.excluz.domain.cartItem.service;

import org.springframework.stereotype.Service;

import excluz.excluz.common.entity.CartItem;
import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.User;
import excluz.excluz.domain.cartItem.dto.request.CreateCartItemRequestDto;
import excluz.excluz.domain.cartItem.dto.response.CreateCartItemResponseDto;
import excluz.excluz.domain.cartItem.repository.CartItemRepository;
import excluz.excluz.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {
	private final CartItemRepository cartItemRepository;
	private final UserRepository userRepository;

	// 물품 추가
	@Transactional
	public CreateCartItemResponseDto addItemToCart(Integer userId, CreateCartItemRequestDto requestDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
		Item item = itemRepository.findById(requestDto.getItemId())
			.orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));

		CartItem cartItem = new CartItem(user, item, requestDto.getQuantity());
		cartItemRepository.save(cartItem);

		return new CreateCartItemResponseDto();
	}

}

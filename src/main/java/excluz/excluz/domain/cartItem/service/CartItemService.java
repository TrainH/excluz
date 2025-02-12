package excluz.excluz.domain.cartItem.service;

import org.springframework.stereotype.Service;

import excluz.excluz.domain.cartItem.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {
	private final CartItemRepository cartItemRepository;
}

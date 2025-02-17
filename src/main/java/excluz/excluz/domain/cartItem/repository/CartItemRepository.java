package excluz.excluz.domain.cartItem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import excluz.excluz.common.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	// 특정 유저의 모든 장바구니 아이템 조회(다건 조회)
	List<CartItem> findByUserId(Integer userId);

	// 특정 유저의 특정 장바구니 아이템 조회(단건 조회)
	Optional<CartItem> findByIdAndUserId(Integer cartItemId, Integer userId);

	// 특정 유저의 장바구니에 특정 아이템이 있는지 조회(단건 조회)
	Optional<CartItem> findByUserIdAndItemId(Integer userId, Integer itemId);
}

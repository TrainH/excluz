package excluz.excluz.domain.cartItem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import excluz.excluz.common.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	// 특정 유저의 모든 장바구니 아이템 조회(다건 조회)
	List<CartItem> findByUserId(Integer userId);

}

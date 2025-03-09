package excluz.excluz.domain.cartItem.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import excluz.excluz.common.entity.CartItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	// 특정 유저의 모든 장바구니 아이템 조회(다건 조회)
	@Query(
		value = "SELECT ci FROM CartItem ci " +
			"LEFT JOIN FETCH ci.item " +
			"LEFT JOIN FETCH ci.item.store " +
			"LEFT JOIN FETCH ci.user " +
			"WHERE ci.user.id = :userId",
		countQuery = "SELECT count(ci) FROM CartItem ci WHERE ci.user.id = :userId"
	)
	Page<CartItem> findByUserId(@Param("userId") Integer userId, Pageable pageable);

	// 특정 유저의 특정 장바구니 아이템 조회(단건 조회)
	Optional<CartItem> findByIdAndUserId(Integer cartItemId, Integer userId);

	// 특정 유저의 장바구니에 특정 아이템이 있는지 조회(단건 조회)
	Optional<CartItem> findByUserIdAndItemId(Integer userId, Integer itemId);
}

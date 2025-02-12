package excluz.excluz.domain.cartItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import excluz.excluz.common.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}

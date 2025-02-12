package excluz.excluz.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name="cart_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

	// CartItem 식별자
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// 유저 식별자(외래키)
	@ManyToOne(
		fetch = FetchType.LAZY,
		optional = false // user가 항상 필수 필드인 것을 명시
	)
	@JoinColumn(
		name = "user_id",
		nullable = false
	)
	private User user;

	// 아이템 식별자(외래키)
	@ManyToOne(
		fetch = FetchType.LAZY,
		optional = false // item이 항상 필수 필드인 것을 명시
	)
	@JoinColumn(
		name = "item_id",
		nullable = false
	)
	private Item item;

	// 개수
	@Column(name = "quantity", nullable = false)
	private int quantity;
}

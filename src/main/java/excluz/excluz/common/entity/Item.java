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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter // todo: (최원준 02-13) 유닛 테스트를 위한 임시 세팅. 추후 지울 것
@Getter
@Entity
@Table(name = "items")
@NoArgsConstructor
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	@Column(name = "item_name", nullable = false, length = 50)
	private String itemName;

	private String explanation;

	@Column(nullable = false)
	private Integer price;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "remaining_quantity", nullable = false)
	private Integer remainingQuantity; // 잔여수량

	@Builder
	public Item(Store store,
				String itemName,
				String explanation,
				Integer price,
				Integer remainingQuantity) {
		this.store = store;
		this.itemName=itemName;
		this.explanation = explanation;
		this.price = price;
		this.remainingQuantity = remainingQuantity;
		this.isDeleted = false;
	}

	public void updateIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void updateRemainingQuantity(Integer remainingQuantity) { this.remainingQuantity = remainingQuantity;
	}
}

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

	public void updateRemainingQuantity(Integer remainingQuantity) { this.remainingQuantity = remainingQuantity; }

  public void addRemainingQuantity(Integer amount) {
    if (amount <= 0) {
        throw new IllegalArgumentException("추가 수량은 0보다 커야 합니다."); // 임시 예외처리
    }
        this.remainingQuantity += amount;
  }


  public void removeRemainingQuantity(Integer amount) {
    if (amount <= 0) {
        throw new IllegalArgumentException("차감 수량은 0보다 커야 합니다.");// 임시 예외처리
    }
    if (this.remainingQuantity < amount) {
        throw new IllegalArgumentException("잔여 수량이 부족합니다."); // 임시 예외처리
    }
      this.remainingQuantity -= amount;
  }
                                                                  
	public void updateItem(
		String itemName,
		String explanation,
		Integer price,
		Integer remainingQuantity
	) {
		if(itemName!=null) this.itemName=itemName;
		if(explanation!=null) this.explanation=explanation;
		if(price!=null && price>=0) this.price=price;
		if(remainingQuantity!=null && remainingQuantity>=0) this.remainingQuantity=remainingQuantity;
	}
}

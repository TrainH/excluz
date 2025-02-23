package excluz.excluz.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name="store_revenues")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreRevenue extends BaseEntity {

	// StoreRevenue 식별자
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(
		fetch = FetchType.LAZY,
		optional = false // store가 항상 필수 필드인 것을 명시
	)
	@JoinColumn(
		name = "store_id",
		nullable = false
	)
	private Store store;

	// 총 매출
	@Column(name = "total_revenue", nullable = false)
	private Long totalRevenue = 0L;

	// 매개변수 4개 미만일 때는 직접 생성자 추가
	public StoreRevenue(Store store, Long totalRevenue) {
		this.store = store;
		this.totalRevenue = totalRevenue;
	}

	// 매출 증가
	public void addRevenue(Long amount) {
		this.totalRevenue += amount;
	}
}
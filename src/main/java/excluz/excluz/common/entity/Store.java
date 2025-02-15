package excluz.excluz.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "stores")
@NoArgsConstructor
public class Store extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne
	@JoinColumn(name = "streamer_id")
	private Streamer streamer;

	@Column(length = 100, nullable = false)
	private String address;

	@Column(length = 30, unique = true, nullable = false)
	private String storeName;

	@Column(length = 30, unique = true, nullable = false)
	private String registrationNumber;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Builder
	public Store(Streamer streamer,
		String address,
		String storeName,
		String registrationNumber) {
		this.streamer = streamer;
		this.address = address;
		this.registrationNumber = registrationNumber;
		this.storeName = storeName;
		this.isDeleted = false; //삭제되지 않은 상태로 설정(기본값)
	}

	public void updateIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void updateStore(
		String address,
		String storeName,
		String registrationNumber
	) {
		if (address != null) this.address = address;
		if (storeName != null) this.storeName = storeName;
		if (registrationNumber != null) this.registrationNumber = registrationNumber;
	}
}

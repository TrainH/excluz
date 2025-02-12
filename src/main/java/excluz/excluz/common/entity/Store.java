package excluz.excluz.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "stores")
@NoArgsConstructor
public class Store extends BaseEntity{

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer storeId;

	@Column(length = 100)
	private String address;

	@Column(length = 30, unique = true)
	private String storeName;

	@Column(length = 30, unique = true)
	private String registrationNumber;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	public Store(String address, String storeName, String registrationNumber){
		this.address=address;
		this.registrationNumber=registrationNumber;
		this.storeName=storeName;
		this.isDeleted=false; //삭제되지 않은 상태로 설정(기본값)
	}

	public void updateIsDeleted(boolean isDeleted){
		this.isDeleted=isDeleted;
	}
}

package excluz.excluz.domain.store.store.dto.response;

import excluz.excluz.common.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreResponseDto {

	private Integer storeId;
	private String address;
	private String storeName;
	private String registrationNumber;

	public StoreResponseDto(Integer storeId,String address, String storeName, String registrationNumber) {
		this.storeId=storeId;
		this.address = address;
		this.storeName = storeName;
		this.registrationNumber = registrationNumber;
	}

	public static StoreResponseDto from(Store store) {
		return new StoreResponseDto(
			store.getId(),
			store.getAddress(),
			store.getStoreName(),
			store.getRegistrationNumber());
	}
}

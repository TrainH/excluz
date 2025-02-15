package excluz.excluz.domain.store.store.dto.response;

import excluz.excluz.common.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreUpdateResponseDto {

	private String address;
	private String storeName;
	private String registrationNumber;

	public StoreUpdateResponseDto(String address, String storeName, String registrationNumber) {
		this.address = address;
		this.storeName = storeName;
		this.registrationNumber = registrationNumber;
	}

	public static StoreUpdateResponseDto from(Store store) {
		return new StoreUpdateResponseDto(
			store.getAddress(),
			store.getStoreName(),
			store.getRegistrationNumber());
	}
}

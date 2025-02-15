package excluz.excluz.domain.store.store.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreUpdateRequestDto {

	private String address;
	private String storeName;
	private String registrationNumber;

	public StoreUpdateRequestDto(String address, String storeName, String registrationNumber) {
		this.address=address;
		this.storeName=storeName;
		this.registrationNumber=registrationNumber;
	}
}

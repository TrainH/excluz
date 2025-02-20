package excluz.excluz.domain.store.store.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreNameResponseDto {

	private Integer storeId;
	private String storeName;

	public StoreNameResponseDto(String storeName, Integer storeId) {
		this.storeId=storeId;
		this.storeName=storeName;
	}
}

package excluz.excluz.domain.store.store.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreResponseDto {

	private String storeName;

	public StoreResponseDto(String storeName) {
		this.storeName=storeName;
	}
}

package excluz.excluz.domain.store.store.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import excluz.excluz.common.entity.Store;
import excluz.excluz.domain.store.item.dto.response.ItemResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreDetailResponseDto {

	private String nickName;
	private String address;
	private String storeName;
	private String registrationNumber;
	private Page<ItemResponseDto> itemList;

	@Builder
	public StoreDetailResponseDto(
		String nickName,
		String address,
		String storeName,
		String registrationNumber,
		Page<ItemResponseDto> itemList
	) {
		this.nickName = nickName;
		this.address = address;
		this.storeName = storeName;
		this.registrationNumber = registrationNumber;
		this.itemList = itemList;
	}

	public static StoreDetailResponseDto of(
		String nickName,
		Store store,
		Page<ItemResponseDto> itemList
	) {
		return StoreDetailResponseDto.builder()
			.nickName(nickName)
			.address(store.getAddress())
			.storeName(store.getStoreName())
			.registrationNumber(store.getRegistrationNumber())
			.itemList(itemList)
			.build();
	}
}

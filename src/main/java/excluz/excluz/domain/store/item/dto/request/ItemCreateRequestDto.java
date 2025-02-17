package excluz.excluz.domain.store.item.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemCreateRequestDto {

	@NotBlank(message = "굿즈 이름은 필수 입력값 입니다.")
	private String itemName;

	private String explanation;

	@Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
	private Integer price;

	@Min(value = 0, message = "잔여수량은 0개 이상이어야 합니다.")
	private Integer remainingQuantity;

	@Builder
	public ItemCreateRequestDto(
		String itemName,
		String explanation,
		Integer price,
		Integer remainingQuantity
	) {
		this.itemName=itemName;
		this.explanation=explanation;
		this.price=price;
		this.remainingQuantity=remainingQuantity;
	}
}

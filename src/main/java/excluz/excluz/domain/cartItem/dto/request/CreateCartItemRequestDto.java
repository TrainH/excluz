package excluz.excluz.domain.cartItem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCartItemRequestDto {
	@NotNull(message = "아이템 ID는 필수입니다.")
	private Integer itemId;

	@Min(value = 1, message = "수량은 1 이상이어야 합니다.")
	private Integer quantity;

	public CreateCartItemRequestDto(Integer itemId, Integer quantity) {
		this.itemId = itemId;
		this.quantity = quantity;
	}
}

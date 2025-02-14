package excluz.excluz.domain.cartItem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCartItemQuantityRequestDto {
	// 장바구니 아이템 개수
	@NotNull(message = "수량은 필수 입력값입니다.")
	@Min(value = 1, message = "수량은 1 이상이어야 합니다.") // 최소 개수 제한 (1개 이상)
	private Integer quantity;

	public UpdateCartItemQuantityRequestDto(Integer quantity) {
		this.quantity = quantity;
	}
}
package excluz.excluz.domain.cartItem.dto.response;

import lombok.Getter;

@Getter
public class CreateCartItemResponseDto {
	private final String message;

	public CreateCartItemResponseDto() {
		this.message = "장바구니에 굿즈가 담겼습니다.";
	}
}

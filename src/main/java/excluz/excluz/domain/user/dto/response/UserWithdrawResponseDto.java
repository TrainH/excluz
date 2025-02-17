package excluz.excluz.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserWithdrawResponseDto {

	private String message;

	public UserWithdrawResponseDto(String message) {
		this.message = message;
	}
}

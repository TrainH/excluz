package excluz.excluz.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserLoginResponseDto {

	private final String message;
	private final String token;

	public UserLoginResponseDto(String message, String token) {
		this.message = message;
		this.token = token;
	}
}

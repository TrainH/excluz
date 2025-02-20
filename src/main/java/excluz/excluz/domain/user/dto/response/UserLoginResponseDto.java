package excluz.excluz.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserLoginResponseDto {

	private final String token;

	public UserLoginResponseDto(String token) {
		this.token = token;
	}
}

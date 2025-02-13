package excluz.excluz.domain.user.dto.response;

import java.time.LocalDateTime;

import excluz.excluz.common.entity.User;
import lombok.Getter;

@Getter
public class UserSignupResponseDto {

	private final String message;
	private final LocalDateTime createdAt;

	public UserSignupResponseDto(String message, User user) {
		this.message = message;
		this.createdAt = user.getCreatedAt();
	}
}

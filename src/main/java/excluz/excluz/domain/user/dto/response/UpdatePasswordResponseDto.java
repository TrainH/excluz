package excluz.excluz.domain.user.dto.response;

import java.time.LocalDateTime;

import excluz.excluz.common.entity.User;
import lombok.Getter;

@Getter
public class UpdatePasswordResponseDto {

	private final String message;
	private final LocalDateTime updatedAt;

	public UpdatePasswordResponseDto(String message, User user) {
		this.message = message;
		this.updatedAt = user.getUpdatedAt();
	}
}

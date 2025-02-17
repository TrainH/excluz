package excluz.excluz.domain.user.dto.response;

import java.time.LocalDateTime;

import excluz.excluz.common.entity.User;
import lombok.Getter;

@Getter
public class UpdateMyProfileResponseDto {

	private final String message;
	private final LocalDateTime updatedAt;

	public UpdateMyProfileResponseDto(String message, User user) {

		this.message = message;
		this.updatedAt = user.getUpdatedAt();

	}
}

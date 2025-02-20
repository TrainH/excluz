package excluz.excluz.domain.user.dto.response;

import java.time.LocalDateTime;

import excluz.excluz.common.entity.User;
import lombok.Getter;

@Getter
public class UpdatePasswordResponseDto {

	private final LocalDateTime updatedAt;

	public UpdatePasswordResponseDto(User user) {
		this.updatedAt = user.getUpdatedAt();
	}
}

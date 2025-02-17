package excluz.excluz.domain.user.dto.response;

import excluz.excluz.common.entity.User;
import lombok.Getter;

@Getter
public class UserProfileResponseDto {

	private final String nickName;
	private final String email;

	public UserProfileResponseDto(User user) {
		this.nickName = user.getNickName();
		this.email = user.getEmail();
	}
}

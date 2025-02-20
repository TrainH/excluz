package excluz.excluz.domain.user.dto.response;

import java.time.LocalDateTime;

import excluz.excluz.common.entity.User;
import lombok.Getter;

@Getter
public class UpdateMyProfileResponseDto {

	private final String nickname;
	private final String phoneNumber;
	private final String address;
	private final LocalDateTime updatedAt;

	public UpdateMyProfileResponseDto(User user) {

		this.nickname = user.getNickName();
		this.phoneNumber = user.getPhoneNumber();
		this.address = user.getAddress();
		this.updatedAt = user.getUpdatedAt();

	}
}

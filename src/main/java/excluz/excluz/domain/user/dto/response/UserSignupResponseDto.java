package excluz.excluz.domain.user.dto.response;

import java.time.LocalDateTime;

import excluz.excluz.common.entity.User;
import lombok.Getter;

@Getter
public class UserSignupResponseDto {

	private final String name;
	private final String nickName;
	private final String phoneNumber;
	private final String address;
	private final String email;
	private final LocalDateTime createdAt;

	public UserSignupResponseDto(User user) {
		this.name = user.getName();
		this.nickName = user.getNickName();
		this.phoneNumber = user.getPhoneNumber();
		this.address = user.getAddress();
		this.email = user.getEmail();
		this.createdAt = user.getCreatedAt();
	}
}

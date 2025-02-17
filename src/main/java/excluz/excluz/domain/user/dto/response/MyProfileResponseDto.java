package excluz.excluz.domain.user.dto.response;

import java.time.LocalDateTime;

import excluz.excluz.common.entity.User;
import lombok.Getter;

@Getter
public class MyProfileResponseDto {

	private final String name;
	private final String nickName;
	private final String email;
	private final String phoneNumber;
	private final String address;
	private final LocalDateTime createdAt;

	public MyProfileResponseDto(User user) {
		this.name = user.getName();
		this.nickName = user.getNickName();
		this.email = user.getEmail();
		this.phoneNumber = user.getPhoneNumber();
		this.address = user.getAddress();
		this.createdAt = user.getCreatedAt();
	}
}

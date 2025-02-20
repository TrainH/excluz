package excluz.excluz.domain.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateMyProfileRequestDto {

	private final String nickName;
	private final String phoneNumber;
	private final String address;
	private final String password;

	@Builder
	public UpdateMyProfileRequestDto(
			String nickName,
			String phoneNumber,
			String address,
			String password) {
		this.nickName = nickName;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.password = password;
	}
}

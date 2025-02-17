package excluz.excluz.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserWithdrawRequestDto {

	private final String password;
	private final String reEnterPassword;

	public UserWithdrawRequestDto(String password, String reEnterPassword) {
		this.password = password;
		this.reEnterPassword = reEnterPassword;
	}
}

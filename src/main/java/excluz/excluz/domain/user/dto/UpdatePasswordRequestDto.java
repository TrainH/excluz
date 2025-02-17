package excluz.excluz.domain.user.dto;

import lombok.Getter;

@Getter
public class UpdatePasswordRequestDto {

	private final String oldPassword;
	private final String newPassword;
	private final String reEnterPassword;

	public UpdatePasswordRequestDto(
			String oldPassword,
			String newPassword,
			String reEnterPassword) {
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.reEnterPassword = reEnterPassword;
	}
}

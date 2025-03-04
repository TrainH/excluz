package excluz.excluz.domain.emailVerification.dto;

import lombok.Data;

@Data
public class EmailVerifyRequestDto {

	private String email;

	private String verifyCode;
}

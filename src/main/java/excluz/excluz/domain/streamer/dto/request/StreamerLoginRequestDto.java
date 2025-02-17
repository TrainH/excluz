package excluz.excluz.domain.streamer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StreamerLoginRequestDto {

	@NotBlank(message = "이메일은 필수 입력값 입니다.")
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "올바른 이메일 형식으로 작성해주세요")
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력값 입니다.")
	private String password;

	public StreamerLoginRequestDto(String email, String password) {
		this.email=email;
		this.password=password;
	}
}

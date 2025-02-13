package excluz.excluz.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSignupRequestDto {

	@NotBlank(message = "이름은 필수 입력값 입니다.")
	private final String name;

	@NotBlank(message = "닉네임을 입력해 주세요.")
	private final String nickName;

	@NotBlank(message = "전화번호는 필수 입력값 입니다.")
	@Pattern(regexp = "010-\\d{4}-\\d{4}", message = "전화번호 형식은 010-xxxx-xxxx 이어야 합니다.")
	private final String phoneNumber;

	@NotBlank(message = "주소는 필수 입력값 입니다.")
	private final String address;

	@NotBlank(message = "이메일은 필수 입력값 입니다.")
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "올바른 이메일 형식으로 작성해주세요")
	private final String email;

	@NotBlank(message = "비밀번호는 필수 입력값 입니다.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{10,30}$", message = "비밀번호는 영문,숫자,특수문자를 포함하여 10자 이상 30자 이내로 작성해 주세요.")
	private final String password;

	@NotBlank(message = "비밀번호를 다시한번 입력해 주세요.")
	private final String reEnterPassword;

}

package excluz.excluz.domain.streamer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StreamerUpdateRequestDto {

	private String name;
	private String nickName;
	private String phoneNumber;
	private String email;

	@NotBlank(message = "본인 확인을 위해 비밀번호를 입력해주세요.")
	private String password;

	@Builder
	public StreamerUpdateRequestDto(
		String name,
		String nickName,
		String phoneNumber,
		String email,
		String password
	) {
		this.name=name;
		this.nickName=nickName;
		this.phoneNumber=phoneNumber;
		this.email=email;
		this.password=password;
	}
}

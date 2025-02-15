package excluz.excluz.domain.streamer.dto.response;

import excluz.excluz.common.entity.Streamer;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StreamerUpdateResponseDto {

	private String name;
	private String nickName;
	private String phoneNumber;
	private String email;

	@Builder
	public StreamerUpdateResponseDto(
		String name,
		String nickName,
		String phoneNumber,
		String email
	) {
		this.name=name;
		this.nickName=nickName;
		this.phoneNumber=phoneNumber;
		this.email=email;
	}

	public static StreamerUpdateResponseDto from(Streamer streamer) {
		return StreamerUpdateResponseDto.builder()
			.name(streamer.getName())
			.nickName(streamer.getNickName())
			.phoneNumber(streamer.getPhoneNumber())
			.email(streamer.getEmail())
			.build();
	}
}

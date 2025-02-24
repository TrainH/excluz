package excluz.excluz.domain.streamer.dto.response;

import excluz.excluz.common.entity.Streamer;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StreamerResponseDto {

	private Integer streamerId;
	private String name;
	private String nickName;
	private String phoneNumber;
	private String email;

	@Builder
	public StreamerResponseDto(
		Integer streamerId,
		String name,
		String nickName,
		String phoneNumber,
		String email
	) {
		this.streamerId=streamerId;
		this.name=name;
		this.nickName=nickName;
		this.phoneNumber=phoneNumber;
		this.email=email;
	}

	public static StreamerResponseDto from(Streamer streamer) {
		return StreamerResponseDto.builder()
			.streamerId(streamer.getId())
			.name(streamer.getName())
			.nickName(streamer.getNickName())
			.phoneNumber(streamer.getPhoneNumber())
			.email(streamer.getEmail())
			.build();
	}
}

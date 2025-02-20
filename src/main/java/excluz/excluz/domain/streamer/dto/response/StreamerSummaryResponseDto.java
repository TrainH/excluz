package excluz.excluz.domain.streamer.dto.response;

import excluz.excluz.common.entity.Streamer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StreamerSummaryResponseDto {

	private Integer streamerId;
	private String nickName;

	public StreamerSummaryResponseDto(String nickName, Integer streamerId) {
		this.nickName = nickName;
		this.streamerId = streamerId;
	}

	public static StreamerSummaryResponseDto from(Streamer streamer) {
		return new StreamerSummaryResponseDto(streamer.getNickName(), streamer.getId());
	}
}

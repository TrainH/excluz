package excluz.excluz.domain.streamer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StreamerLoginResponseDto {

	private final String token;

	public static StreamerLoginResponseDto from(String token){
		return new StreamerLoginResponseDto(token);
	}
}

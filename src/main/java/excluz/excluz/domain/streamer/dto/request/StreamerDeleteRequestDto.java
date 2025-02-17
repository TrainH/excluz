package excluz.excluz.domain.streamer.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StreamerDeleteRequestDto {

	private String password;

	public StreamerDeleteRequestDto(String password) {
		this.password=password;
	}
}

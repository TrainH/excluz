package excluz.excluz.common.exception.error;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponseDto {
	private final HttpStatus httpStatus;
	private final String errorMessage;

	public ErrorResponseDto(ErrorCode errorCode) {
		this(errorCode.getStatus(), errorCode.getMessage());
	}
}

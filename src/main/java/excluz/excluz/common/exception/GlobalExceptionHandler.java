package excluz.excluz.common.exception;

import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import excluz.excluz.common.exception.error.ErrorResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(CustomRuntimeException.class)
	protected ResponseEntity<ErrorResponseDto> handleCustomException(final CustomRuntimeException e) {
		ErrorResponseDto response = new ErrorResponseDto(e.getErrorCode());
		return createResponseEntity(response);
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ErrorResponseDto> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
		ErrorResponseDto response = new ErrorResponseDto(HttpStatus.FORBIDDEN, e.getMessage());
		return createResponseEntity(response);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(final IllegalArgumentException e) {
		ErrorResponseDto response = new ErrorResponseDto(HttpStatus.BAD_REQUEST, e.getMessage());
		return createResponseEntity(response);
	}

	// 유효성 검사(validation) 통과하지 못할시 발생
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
		String errorMessages = e.getBindingResult().getAllErrors()
			.stream()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.collect(Collectors.joining("; "));
		ErrorResponseDto response = new ErrorResponseDto(HttpStatus.BAD_REQUEST, errorMessages);
		return createResponseEntity(response);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
		ErrorResponseDto response = new ErrorResponseDto(HttpStatus.BAD_REQUEST, e.getMessage());
		return createResponseEntity(response);
	}

	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<ErrorResponseDto> handleRuntimeException(final RuntimeException e) {
		ErrorResponseDto response = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		return createResponseEntity(response);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponseDto> handleException(final Exception e) {
		ErrorResponseDto response = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		return createResponseEntity(response);
	}

	private ResponseEntity<ErrorResponseDto> createResponseEntity(ErrorResponseDto response) {
		return new ResponseEntity<>(response, response.getHttpStatus());
	}
}

package excluz.excluz.common.exception.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// 유저, 스트리머 관련 예외 코드
	TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Token Not Found"),
	FORBIDDEN_USER_ACCESS(HttpStatus.FORBIDDEN, "사용자의 리소스에 접근할 권한이 없습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "조회되는 회원 정보가 없습니다."),
	UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "가입되지 않은 회원입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 혹은 기존과 동일한 비밀번호입니다."),

	// 아이템 관련 예외 코드
	ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "조회되는 아이템 정보가 없습니다.");

	// 하단에 에러코드 추가하여 사용

	private final HttpStatus status;
	private final String message;
}

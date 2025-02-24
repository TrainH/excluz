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
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
	UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "가입되지 않은 회원입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "이전과 동일한 비밀번호로 수정할 수 없습니다."),
	PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
	EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다."),
	PASSWORD_RE_ENTER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호와 재입력 비밀번호가 일치하지 않습니다."),

	// 아이템 관련 예외 코드
	ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "조회되는 아이템 정보가 없습니다."),
	OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "해당 아이템의 재고가 부족합니다."),
	ITEM_NOT_MATCH(HttpStatus.BAD_REQUEST, "아이템이 현재 스토어에 소속되어 있지 않습니다"),

	// 장바구니 아이템 관련 예외 코드
	CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니에 해당 아이템이 존재하지 않습니다."),

	//스토어 관련 예외 코드
	STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "스토어 정보를 찾을 수 없습니다."),
	STORE_NOT_MATCH(HttpStatus.BAD_REQUEST, "스토어에 대한 권한이 없습니다"),
	DUPLICATE_REGISTRATION_NUMBER(HttpStatus.CONFLICT, "다른 스토어와 중복되는 사업자 등록번호입니다."),
	STORE_ALREADY_EXIST(HttpStatus.CONFLICT, "운영중인 스토어가 이미 존재합니다."),

	// 포인트 관련 예외 코드
	POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "포인트를 충전해주세요."),

	// 포인트 거래 관련 예외 코드

	// 주운 아이템 관련 예외 코드
	ORDER_ITEM_ADDRESS_MISMATCH(HttpStatus.BAD_REQUEST, "주문 배달 주소는 모두 동일해야합니다."),
	ORDER_ITEM_CART_ITEM_QUANTITIES_MISMATCH(HttpStatus.BAD_REQUEST,"요청된 주문 아이템 수량과 장바구니의 아이템수량이 일치 않습니다."),
	ORDER_ITEM_STORE_MISMATCH(HttpStatus.BAD_REQUEST, "주문된 아이템들의 가게가 동일해야합니다."),

	// 주문 관련 예외 코드
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),
	ORDER_STATUS_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "주문 상태 변경이 불가능합니다."),

	//	이벤트 관련 예외 코드
	EVENT_ENDDATETIME_TOO_EARLY(HttpStatus.BAD_REQUEST, "이벤트 종료일이 현재 시간보다 과거일 수 없습니다."),
	EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다."),
	EVENT_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "이미 마감된 이벤트입니다"),
	EVENT_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "취소된 이벤트입니다"),
	EVENT_METHOD_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "지원되지 않는 선정 방식입니다"),
	EVENT_APPLICANT_NOT_STARTED(HttpStatus.BAD_REQUEST, "이벤트가 아직 시작되지 않았습니다"),
	EVENT_APPLICANT_EXPIRED(HttpStatus.BAD_REQUEST, "이벤트가 이미 종료되었습니다."),
	EVENT_APPLICANT_NOT_FOUND(HttpStatus.NOT_FOUND, "응모 정보를 찾을 수 없거나 잘못된 인증 정보입니다."),
	EVENT_APPLICANT_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "이미 수령 확정한 응모를 취소할 수 없습니다."),
	EVENT_APPLICANT_NOT_WINNER(HttpStatus.BAD_REQUEST, "당첨(WINNER) 상태가 아닌 유저의 수령 확정은 불가능합니다.")
	;

	// 하단에 에러코드 추가하여 사용

	private final HttpStatus status;
	private final String message;
}

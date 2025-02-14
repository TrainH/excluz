package excluz.excluz.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
	ADMIN("ROLE_ADMIN", "관리자"), // 관리자
	CUSTOMER("ROLE_CUSTOMER", "일반 회원"), // 일반 회원
	STREAMER("ROLE_STREAMER", "굿즈 판매자") // 굿즈 판매자
	;
	private final String role;
	private final String description;
}

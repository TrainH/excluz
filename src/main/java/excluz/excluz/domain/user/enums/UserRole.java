package excluz.excluz.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
	ADMIN("ADMIN", "관리자"), // 관리자
	CUSTOMER("CUSTOMER", "일반 회원"), // 일반 회원
	STREAMER("STREAMER", "굿즈 판매자") // 굿즈 판매자
	;
	private final String role;
	private final String description;
}

package excluz.excluz.auth.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.extern.slf4j.Slf4j;

public class SecurityContextUtil {

	public static UserRole getUserRole() {
		GrantedAuthority grantedAuthority = SecurityContextHolder.getContext()
			.getAuthentication()
			.getAuthorities()
			.stream().findFirst().orElse(null);

		if(grantedAuthority == null) {
			return null;
		}

		return UserRole.valueOf(grantedAuthority.getAuthority());
	}

	public static Integer getUserId() {
		String userId = SecurityContextHolder.getContext()
			.getAuthentication()
			.getName();

		if (userId == null) {
			return null;
		}

		return Integer.valueOf(userId);
	}
}

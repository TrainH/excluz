package excluz.excluz.auth.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


import excluz.excluz.domain.user.enums.UserRole;

public class SecurityContextUtil {

	public static UserRole getUserRole() {
		GrantedAuthority grantedAuthority = SecurityContextHolder.getContext()
			.getAuthentication()
			.getAuthorities()
			.stream().findFirst().orElse(null);

		if(grantedAuthority == null) {
			return null;
		}

		String role = grantedAuthority.getAuthority().replace("ROLE_", "");

		return UserRole.valueOf(role);
	}

	public static Integer getUserOrStreamerId() {
		String userId = SecurityContextHolder.getContext()
			.getAuthentication()
			.getName();

		if (userId == null) {
			return null;
		}

		return Integer.valueOf(userId);
	}
}

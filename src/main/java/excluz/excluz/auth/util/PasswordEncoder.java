package excluz.excluz.auth.util;

import org.springframework.stereotype.Component;
import at.favre.lib.crypto.bcrypt.BCrypt;

@Component
public class PasswordEncoder {

	// Bcrypt : 단방향 해시 알고리즘

	// 일반 문자열을 암호화
	public String encode(String rawPassword) {
		return BCrypt.withDefaults().hashToString(BCrypt.MIN_COST, rawPassword.toCharArray());
	}

	public boolean matches(String rawPassword, String encodedPassword) {
		BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword);
		return result.verified;
	}
}
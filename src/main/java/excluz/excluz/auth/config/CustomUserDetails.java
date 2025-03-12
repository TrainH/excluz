package excluz.excluz.auth.config;

import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, OAuth2User {

	private final String email;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;
	private final Integer id;

	// OAuth2User에서 요구하는 속성을 저장할 필드
	private Map<String, Object> attributes;

	// OAuth2User 인터페이스 구현: getAttributes()
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return String.valueOf(id);
	}

	// UserDetails 인터페이스 메서드들은 이미 구현되어 있음.
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}

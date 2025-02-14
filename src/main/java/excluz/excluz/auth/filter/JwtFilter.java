package excluz.excluz.auth.filter;

import excluz.excluz.auth.util.JwtUtil;
import excluz.excluz.domain.user.enums.UserRole;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;
import java.util.List;

@Slf4j(topic = "JwtFilter")
@RequiredArgsConstructor
@Component
public class JwtFilter implements Filter {

	private final JwtUtil jwtUtil;

	private static final String[] SIGN_UP_URI = {
		"/api/v1/users/signup",
		"/api/v1/streamers/signup"
	};

	private static final String[] SIGN_IN_URI = {
		"/api/v1/users/login",
		"/api/v1/streamers/login"
	};

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String requestURI = httpRequest.getRequestURI();

		if (isSignUpURI(requestURI) || isSignInURI(requestURI)) {
			chain.doFilter(request, response);
			return;
		}

		String authorizationHeader = httpRequest.getHeader("Authorization");

		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			log.info("JWT 토큰이 필요 합니다.");
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 필요 합니다.");
			return;
		}
		String jwt = authorizationHeader.substring(7);

		if (!jwtUtil.validateToken(jwt)) {
			httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			httpResponse.getWriter().write("{\"error\": \"Unauthorized\"}");
		}

		String userId = jwtUtil.extractUserId(jwt);
		String auth = jwtUtil.extractRoles(jwt);
		UserRole userRole = UserRole.valueOf(auth);
		User user = new User(userId,"", List.of(userRole::getRole));

		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

		chain.doFilter(request, response);
	}

	// requestURI가 회원가입 URI인지 확인
	public boolean isSignUpURI(String requestURI) {
		return PatternMatchUtils.simpleMatch(SIGN_UP_URI, requestURI);
	}

	// requestURI가 로그인 URI인지 확인
	public boolean isSignInURI(String requestURI) {
		return PatternMatchUtils.simpleMatch(SIGN_IN_URI, requestURI);
	}


}

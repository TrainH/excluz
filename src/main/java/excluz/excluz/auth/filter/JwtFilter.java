package excluz.excluz.auth.filter;

import excluz.excluz.auth.util.JwtUtil;
import excluz.excluz.domain.user.enums.UserRole;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.List;

@Slf4j(topic = "JwtFilter")
@RequiredArgsConstructor
public class JwtFilter implements Filter {

	private final JwtUtil jwtUtil;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String requestURI = httpRequest.getRequestURI();

		// 1) 우선 Authorization 헤더 확인
		String authorizationHeader = httpRequest.getHeader("Authorization");

		String jwt = null;
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			// 헤더에서 토큰 추출
			jwt = authorizationHeader.substring(7);
		} else {
			// 2) 헤더에 없다면, 쿠키에서 jwtToken을 찾는다
			if (httpRequest.getCookies() != null) {
				for (Cookie cookie : httpRequest.getCookies()) {
					if ("jwtToken".equals(cookie.getName())) {
						jwt = cookie.getValue();
						break;
					}
				}
			}
		}

		// Authorization 헤더도 없고 쿠키도 없으면 필터 종료
		if (jwt == null) {
			chain.doFilter(request, response);
			return;
		}

		// 토큰 유효성 검증
		if (!jwtUtil.validateToken(jwt)) {
			httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			httpResponse.getWriter().write("""
					{"error": "Unauthorized"}""");
			return;
		}

		// 토큰에서 userId, userRole 추출하여 SecurityContext 에 세팅
		String userId = jwtUtil.extractUserId(jwt);
		String roleStr = jwtUtil.extractRoles(jwt);
		UserRole userRole = UserRole.valueOf(roleStr);

		User user = new User(userId, "", List.of(userRole::getRole));
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

		chain.doFilter(request, response);
	}
}

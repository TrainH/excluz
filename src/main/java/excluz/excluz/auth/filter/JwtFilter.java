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

		String authorizationHeader = httpRequest.getHeader("Authorization");

		if (authorizationHeader == null) {
			chain.doFilter(request, response);
			return;
		}

		if (!authorizationHeader.startsWith("Bearer ")) {
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
}

package excluz.excluz.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import excluz.excluz.auth.filter.JwtFilter;
import excluz.excluz.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

	private final JwtUtil jwtUtil;

	@Bean
	public JwtFilter jwtFilter() {
		return new JwtFilter(jwtUtil);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.httpBasic(httpBasic -> httpBasic.disable())
			.formLogin(formLogin -> formLogin.disable())
			// oauth2Login 관련 설정 제거: 컨트롤러 방식으로 처리합니다.
			.authorizeHttpRequests(auth -> auth
				// 보호가 필요한 엔드포인트는 역할 기반 접근 제어 설정
				.requestMatchers(
					"/api/v1/stores/my-store",
					"/api/v1/stores/my-store/soft",
					"/api/v1/streamers/profile",
					"/api/v1/streamers/soft",
					"/api/v1/items/{itemsId}/soft"
				).hasRole("STREAMER")
				// oauth/kakao 등 OAuth2 관련 콜백 및 기타 공개 엔드포인트는 permitAll 설정
				.requestMatchers(
						"/",
					"/actuator/health",
					"/api/v1/users/login",
					"/api/v1/streamers/login",
					"/api/v1/users/signup",
					"/api/v1/streamers/signup",
					"/oauth/kakao",
					"/oauth/kakao/**",
					"/kakao/**",
					"api/v1/email/**",
					"/api/v1/users/{userId}",
					"/api/v1/items",
					"/api/v2/items",
					"/api/v3/items",
					"/api/v4/items",
					"/api/v1/items/{itemsId}",
					"/api/v1/stores/{storeId}",
					"/api/v1/stores",
					"/api/v1/streamers",
					"/api/v1/streamers/{streamerId}",
					// 이벤트
					"/api/v1/events/applicants",
						"/api/v1/events/applicants/optimistic",
						"/api/v1/events/applicants/optimistic/logic",// 이벤트 응모 및 조회(단, @RequestParam 코드가 맞을 때)
					"/api/v1/events/applicants/{eventApplicantId}",
					// 랭킹 조회(TOP10)
					"/api/store-ranking/top10"
					).permitAll()
				.requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // 관리자
				.requestMatchers("/api/v1/users/**").hasRole("CUSTOMER") // 일반 회원
				.anyRequest().authenticated()
			);
		// JWT 필터를 SecurityContextHolderAwareRequestFilter 앞에 추가
		http.addFilterBefore(jwtFilter, org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.class);
		return http.build();
	}
}

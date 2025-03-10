package excluz.excluz.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

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

	@Bean
	public JwtFilter jwtFilter(JwtUtil jwtUtil) {
		return new JwtFilter(jwtUtil);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.addFilterBefore(jwtFilter, SecurityContextHolderAwareRequestFilter.class)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers( // 스트리머 권한 필요
					// 스토어
					"/api/v1/stores/my-store",
					"/api/v1/stores/my-store/soft",
					// 스트리머
					"/api/v1/streamers/profile",
					"/api/v1/streamers/soft",
					// 아이템
					"/api/v1/items/{itemsId}/soft"
					// 이벤트
//					"/api/v1/events",
//					"/api/v1/events/{eventId}",
//					"/api/v1/events/{eventId}/eventItems",
//					"/api/v1/events/{eventId}/applicants"
				).hasRole("STREAMER")
				.requestMatchers( // 권한 불필요
					// 상태 체크
					"/actuator/health",
					// 로그인 & 회원가입
					"/api/v1/users/login",
					"/api/v1/streamers/login",
					"/api/v1/users/signup",
					"/api/v1/streamers/signup",
					"/oauth/kakao",
					"/oauth/kakao/**",
					"/kakao/**",
					"api/v1/email/**",
					// 일반 유저
					"/api/v1/users/{userId}",
					// 아이템
					"/api/v1/items",
					"/api/v2/items",
					"/api/v3/items",
					"/api/v4/items",
					"/api/v1/items/{itemsId}",
					// 스토어
					"/api/v1/stores/{storeId}",
					"/api/v1/stores",
					// 스트리머
					"/api/v1/streamers",
					"/api/v1/streamers/{streamerId}",
					// 이벤트
					"/api/v1/events/applicants", // 이벤트 응모 및 조회(단, @RequestParam 코드가 맞을 때)
					"/api/v1/events/applicants/{eventApplicantId}",
					// 랭킹 조회(TOP10)
					"/api/store-ranking/top10"
					).permitAll()
				.requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // 관리자
				.requestMatchers("/api/v1/users/**").hasRole("CUSTOMER") // 일반 회원
				.anyRequest().authenticated()
			)
			.build();
	}
}

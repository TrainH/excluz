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
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.addFilterBefore(jwtFilter, SecurityContextHolderAwareRequestFilter.class)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/api/v1/users/login", // 일반 유저 로그인
					"/api/v1/streamers/login", // 판매자 로그인
					"/api/v1/users/signup", // 일반 유저 회원가입
					"/api/v1/streamers/signup", // 판매자 회원가입
					"/api/v1/users/{userId}",
					"/api/v1/streamers/{streamerId}",
					"/api/v1/items/{itemsId}",
					"/api/v1/stores/{storeId}/?page=&size=",
					"/api/v1/stores?storeName=&page=&size=",
					"/api/v1/streamers?nickName=&page=&size=",
					"/api/v1/events/applicants", // 이벤트 응모 및 조회(단, @RequestParam 코드가 맞을 때)
					"/api/v1/events/applicants/**",
					"/oauth/kakao",
					"/oauth/kakao/**",
					"/kakao/**"
          
					).permitAll()
				.requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // 관리자
				.requestMatchers("/api/v1/users/**").hasRole("CUSTOMER") // 일반 회원
				.anyRequest().authenticated()
			)
			.build();
	}
}

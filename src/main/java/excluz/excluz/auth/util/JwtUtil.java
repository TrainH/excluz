package excluz.excluz.auth.util;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import excluz.excluz.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j(topic = "JwtUtil")
public class JwtUtil {

	// JWT 접두사
	public static final String BEARER_PREFIX = "Bearer ";
	// JWT 토큰 만료시간 (30분)
	private final long TOKEN_TIME = 30 * 60 * 1000L;
	// 주입받은 시크릿 키
	@Value("${jwt.secret.key}")
	private String secretKey;
	// 실제 서명에 사용되는 키 객체
	private static Key key;

	// key 객체 초기화 최신 버전의 JJWT 는 Keys 객체를 사용하여 서명 알고리즘을 설정해줌
	@PostConstruct
	public void init() {
		byte[] decodedKey = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(decodedKey);
	}

	public String createToken(String email, Integer id, UserRole userRole) {
		Date date = new Date();
		// String temp = String.valueOf(id);

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(email)
				.claim("userId", String.valueOf(id)) // userId
				.claim("userRole", userRole) // 사용자 권한 (역할)
				.setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간 설정
				.setIssuedAt(date) // 발급 시간 설정
				.signWith(key) // 비밀 키와 알고리즘으로 서명
				.compact(); // JWT 토큰 생성
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
			.setSigningKey(key) // 비밀 키를 사용하여 서명 검증
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public String extractRoles(String token) {
		return extractAllClaims(token).get("userRole", String.class);
	}

	public String extractUserId(String token) {
		// Claims temp = extractAllClaims(token);
		// return"test";
		return extractAllClaims(token).get("userId", String.class);
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			// 토큰 서명이 잘못되었거나, 잘못된 형식의 JWT 가 전달된 경우
			log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
		} catch (ExpiredJwtException e) {
			// 토큰이 만료된 경우
			log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
		} catch (UnsupportedJwtException e) {
			// 지원되지 않는 JWT 형식이 전달된 경우
			log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
		} catch (IllegalArgumentException e) {
			// JWT 클레임이 비어 있거나 잘못된 형식일 경우
			log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.", e);
		}

		return false;
	}
}

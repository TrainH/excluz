package excluz.excluz.common.filter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

		try {
			// 요청 오딧 로그 기록
			String clientIP = getClientIP(httpRequest);

			// 한글 디코딩
			Map<String, String[]> paramMap = httpRequest.getParameterMap();
			String requestParams = paramMap.entrySet().stream() // 파라미터를 key=value 형식의 문자열로 변환
				.map(entry -> entry.getKey() + "=" +
					URLDecoder.decode(String.join(",", entry.getValue()), StandardCharsets.UTF_8))
				.collect(Collectors.joining("&"));

			log.info("clientIP: {}, userAgent: {}, requestURI: {}, requestMethod: {}, requestParams: {}",
				clientIP, httpRequest.getHeader("User-Agent"), httpRequest.getRequestURI(),
				httpRequest.getMethod(), requestParams);

			filterChain.doFilter(servletRequest, servletResponse);

			// 응답 오딧 로그 기록
			log.info("clientIP: {}, status: {}, requestURI: {}, requestMethod: {}", clientIP, httpResponse.getStatus(), httpRequest.getRequestURI(),
				httpRequest.getMethod());

		}catch (Exception e) {
			log.error("Error in LoggingFilter", e);
			throw e;
		}
	}

	private static String getClientIP(HttpServletRequest httpRequest) {
		String ip = httpRequest.getHeader("X-Forwarded-For"); // 프록시 고려

		if (ip != null && !ip.isEmpty()) { // IPv4만 저장
			String trimmedIP = ip.split(",")[0].trim();
			if (isValidIPv4(trimmedIP)) {
				return trimmedIP;
			}
		}

		String remoteAddr = httpRequest.getRemoteAddr();

		if (remoteAddr.equals("0:0:0:0:0:0:0:1")) return "127.0.0.1"; // localhost IPv4 형식으로 반환

		return isValidIPv4(remoteAddr) ? remoteAddr : "UNKNOWN"; // IP를 알 수 없거나 IPv6인 경우 "UNKNOWN" 반환
	}

	private static boolean isValidIPv4(String ip) {
		return ip.matches(
			"^(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\." +
				"(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])$"
		);
	}
}

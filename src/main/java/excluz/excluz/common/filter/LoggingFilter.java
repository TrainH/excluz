package excluz.excluz.common.filter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

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

	private final LoggingService loggingService;

	public LoggingFilter(LoggingService loggingService) {
		this.loggingService = loggingService;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
		String clientIP = getClientIP(httpRequest);
		String requestURI = httpRequest.getRequestURI();

		// /actuator/health URL에 대한 로깅을 제외
		if (requestURI.contains("/actuator/health")) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		try {
			// 한글 디코딩
			Map<String, String[]> paramMap = httpRequest.getParameterMap();
			String requestParams = paramMap.entrySet().stream() // 파라미터를 key=value 형식의 문자열로 변환
				.map(entry -> entry.getKey() + "=" +
					URLDecoder.decode(String.join(",", entry.getValue()), StandardCharsets.UTF_8))
				.collect(Collectors.joining("&"));

			log.info("[요청] clientIP: {}, userAgent: {}, requestURI: {}, requestMethod: {}, requestParams: {}",
				clientIP, httpRequest.getHeader("User-Agent"), requestURI,
				httpRequest.getMethod(), requestParams);

			filterChain.doFilter(servletRequest, servletResponse);

		} catch (Exception e) {

			log.error("[오류 발생] clientIP: {}, requestURI: {}, requestMethod={}, message={}",
				clientIP, requestURI, httpRequest.getMethod(), e.getMessage(), e);
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 내부 오류 발생");

			loggingService.sendMessage(
				"필터에서 서버 오류",
				e.getClass().getSimpleName(),
				httpRequest.getMethod(),
				requestURI,
				e.getMessage()
			);

		}finally {

			int status = httpResponse.getStatus();

			if (status >= 400) { // 4xx, 5xx 오류 감지
				log.warn("[오류 응답] clientIP: {}, status: {}, requestURI: {}, requestMethod: {}",
					clientIP, status, requestURI, httpRequest.getMethod());
			} else {
				log.info("[응답] clientIP={}, status={}, requestURI={}, requestMethod={}",
					clientIP, status, requestURI, httpRequest.getMethod());
			}

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

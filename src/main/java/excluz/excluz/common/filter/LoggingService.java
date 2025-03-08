package excluz.excluz.common.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoggingService {

	@Value("${slack.webhook-url}")
	private String webhookUrl;

	public void sendMessage(String title, String exceptionName, String requestURI, String method, String message) {
		// 에러 메세지 특수문자 처리
		if (message == null || message.isEmpty()) {
			message = "No error message provided";
		} else {
			message = message.replace("\n", " ") // 개행 문자 제거
				.replace("\r", " ") // 개행 문자 제거
				.replace("\"", "'") // 큰따옴표 → 작은따옴표로 변경
				.replace("\\", ""); // 역슬래시 제거

			// 메세지 길이 제한
			if (message.length() > 3000) {
				message = message.substring(0, 3000) + "... (생략됨)";
			}
		}

		Slack slack = Slack.getInstance();
		String payload = String.format(
			"{\"text\": \"*🚨 %s 발생!*\\n" +
				"*예외 유형:* %s\\n" +
				"*요청 정보:* [%s %s]\\n" +
				"*에러 메시지:* %s\"}",
			title, exceptionName, method, requestURI, message
		);
		try {
			WebhookResponse response = slack.send(webhookUrl, payload);
			log.info("slack 메시지 전송 상태: {}", response);
		} catch (Exception e) {
			log.error("slack 메시지 발송 중 문제가 발생했습니다.");
		}

	}
}

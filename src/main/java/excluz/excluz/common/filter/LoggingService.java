package excluz.excluz.common.filter;

import org.springframework.stereotype.Service;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoggingService {
	final String webhookUrl = "https://hooks.slack.com/services/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";

	public boolean sendMessage(String title, String exceptionName, String requestURI, String method, String message){
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
			log.info("slack 메시지 전송 성공: {}",response);
		} catch (Exception e) {
			log.error("slack 메시지 발송 중 문제가 발생했습니다.");
			throw new RuntimeException(e);
		}

		return true;
	}
}

package excluz.excluz.domain.emailVerification.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import excluz.excluz.common.entity.EmailVerify;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.emailVerification.repository.EmailVerifyRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final EmailVerifyRepository emailVerifyRepository;


	private static class CodeData {
		String code;
		long expiry;

		CodeData(String code, long expiry) {
			this.code = code;
			this.expiry = expiry;
		}
	}

	private final JavaMailSender javaMailSender;
	private static final String senderEmail = "excluzofficial@gmail.com";
	// 이메일을 key로, CodeData를 value로 저장하는 in-memory Map
	private static final Map<String, CodeData> codeMap = new ConcurrentHashMap<>();
	private static final long VALIDITY_DURATION = 300_000;

	// 인증코드 생성 기능
	private String createCode() {

		// 이메일 인증 코드 길이 수
		int codeLength = 6;

		// ThreadLocalRandom을 사용하여 '0'(48) 부터 'z'(122) 사이의 값의 코드를 생성함
		return ThreadLocalRandom.current()
			.ints(48, 123)
			.filter(i -> (i >= 48 && i <= 57) || // 숫자 0 - 9
				(i >= 65 && i <= 90) || // 대문자 A - Z
				(i >= 97 && i <= 122)) // 소문자 a - z
			.limit(codeLength)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
	}

	private String setContext(String code) {
		Context context = new Context(); // 템플릿 내에서 사용할 변수(데이터)를 저장하는 객체
		TemplateEngine templateEngine = new TemplateEngine(); // 템플릿 파일을 처리(렌더링)하는 핵심 엔진 객체
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver(); // 클래스 로더를 이용해서 탬플릿을 찾는 리졸버

		context.setVariable("code", code); // 템플릿에서 사용할 변수 "code"에 전달받은 code 값을 넣는 로직

		// 리졸버 설정
		// prefix, suffix는 템플릿의 경로를 지정 하는 로직
		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCacheable(false);

		templateEngine.setTemplateResolver(templateResolver);

		return templateEngine.process("email", context);
	}

	// 이메일 폼 생성
	private MimeMessage createEmailForm(String email, String authCode) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		message.addRecipients(MimeMessage.RecipientType.TO, email);
		message.setSubject("Excluz 이메일 인증 코드 입니다.");
		message.setFrom(senderEmail);
		message.setText(setContext(authCode), "utf-8", "html");

		return message;
	}

	// 인증코드 이메일 발송
	public void sendEmail(String email) {
		codeMap.remove(email);

		// 인증 코드 발송후 테이블에 인증 상태를 저장하는 로직
		EmailVerify sendCodeEmail = new EmailVerify(email);
		emailVerifyRepository.save(sendCodeEmail);

		// 새로운 코드 생성
		String newCode = createCode();
		long expiryTime = System.currentTimeMillis() + VALIDITY_DURATION;
		codeMap.put(email, new CodeData(newCode, expiryTime));


		try {
			MimeMessage message = createEmailForm(email, newCode);
			javaMailSender.send(message);
		} catch (MessagingException e) {
			throw new BadRequestException(ErrorCode.FAIL_SEND_EMAIL);
		}
	}

	@Transactional
	public boolean verifyEmailCode(String email, String code) {
		CodeData data = codeMap.get(email);

		// 인증 코드가 없거나 만료된 경우
		if (data == null || System.currentTimeMillis() > data.expiry) {
			codeMap.remove(email);
			return false;
		}

		// 입력한 코드가 저장된 코드와 일치하는 경우
		if (data.code.equals(code)) {
			codeMap.remove(email);

			// 이메일 인증 상태 저장 (EmailVerify 테이블 활용)
			EmailVerify emailVerify = emailVerifyRepository
				.findByEmail(email)
				.orElseGet(() -> new EmailVerify(email)); // 없으면 새로 생성
			emailVerify.updateEmailStatus(true);
			emailVerifyRepository.save(emailVerify);

			return true;
		}
		return false;
	}
}
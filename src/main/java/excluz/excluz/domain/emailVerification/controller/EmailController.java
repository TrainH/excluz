package excluz.excluz.domain.emailVerification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.domain.emailVerification.dto.EmailVerifyRequestDto;
import excluz.excluz.domain.emailVerification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/email")
public class EmailController {

	private final EmailService emailService;

	@PostMapping("/send")
	public ResponseEntity<Void> emailSend(@RequestBody EmailVerifyRequestDto emailVerify) {
		log.info("이메일 발송 로직 작동");
		emailService.sendEmail(emailVerify.getEmail());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/verify")
	public ResponseEntity<Boolean> emailVerify(@RequestBody EmailVerifyRequestDto emailVerify) {
		log.info("이메일 인증 로직 작동");
		boolean isVerify = emailService.verifyEmailCode(emailVerify.getEmail(), emailVerify.getVerifyCode());
		return ResponseEntity.ok(isVerify);
	}
}

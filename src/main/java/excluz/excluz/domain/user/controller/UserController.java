package excluz.excluz.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.domain.user.dto.request.UserLoginRequestDto;
import excluz.excluz.domain.user.dto.request.UserSignupRequestDto;
import excluz.excluz.domain.user.dto.response.UserLoginResponseDto;
import excluz.excluz.domain.user.dto.response.UserSignupResponseDto;
import excluz.excluz.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<UserSignupResponseDto> userSignupAPI(
		@Valid
		@RequestBody UserSignupRequestDto signupRequest) {

		UserSignupResponseDto userSignupResponseDto = userService.userSignup(signupRequest);

		return ResponseEntity.ok(userSignupResponseDto);
	}

	@PostMapping("/login")
	public ResponseEntity<UserLoginResponseDto> userLoginAPI(
		@RequestBody UserLoginRequestDto loginRequest) {

		UserLoginResponseDto userLoginResponseDto = userService.userLogin(loginRequest);

		return ResponseEntity.ok(userLoginResponseDto);
	}
}

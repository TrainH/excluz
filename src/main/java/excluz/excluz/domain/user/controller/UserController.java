package excluz.excluz.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.domain.user.dto.request.UserLoginRequestDto;
import excluz.excluz.domain.user.dto.request.UserSignupRequestDto;
import excluz.excluz.domain.user.dto.request.UserWithdrawRequestDto;
import excluz.excluz.domain.user.dto.response.UserLoginResponseDto;
import excluz.excluz.domain.user.dto.response.UserProfileResponseDto;
import excluz.excluz.domain.user.dto.response.UserSignupResponseDto;
import excluz.excluz.domain.user.dto.response.UserWithdrawResponseDto;
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

	@PatchMapping("/withdraw")
	public ResponseEntity<UserWithdrawResponseDto> userUnregisterAPI(
		@AuthenticationPrincipal User user,
		@RequestBody UserWithdrawRequestDto userWithdrawRequest) {

		// 스트링 값으로 저장된 userId를 Integer 로 강제 반환
		Integer userId = Integer.parseInt(user.getUsername());

		UserWithdrawResponseDto userWithdrawResponseDto = userService.userWithdraw(userId, userWithdrawRequest);

		return ResponseEntity.ok(userWithdrawResponseDto);
	}

	// 마이페이지가 아닌 다른 유저의 정보 조회
	@GetMapping("/{userId}")
	public ResponseEntity<UserProfileResponseDto> userProfileFindAPI(
		@PathVariable(name = "userId") Integer userId){

		UserProfileResponseDto profileResponseDto = userService.getProfile(userId);

		return ResponseEntity.ok(profileResponseDto);
	}
}

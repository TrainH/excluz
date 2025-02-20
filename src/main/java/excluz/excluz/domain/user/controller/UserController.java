package excluz.excluz.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.user.dto.request.UpdatePasswordRequestDto;
import excluz.excluz.domain.user.dto.request.UpdateMyProfileRequestDto;
import excluz.excluz.domain.user.dto.request.UserLoginRequestDto;
import excluz.excluz.domain.user.dto.request.UserSignupRequestDto;
import excluz.excluz.domain.user.dto.request.UserWithdrawRequestDto;
import excluz.excluz.domain.user.dto.response.MyProfileResponseDto;
import excluz.excluz.domain.user.dto.response.UpdateMyProfileResponseDto;
import excluz.excluz.domain.user.dto.response.UpdatePasswordResponseDto;
import excluz.excluz.domain.user.dto.response.UserLoginResponseDto;
import excluz.excluz.domain.user.dto.response.UserProfileResponseDto;
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

	@DeleteMapping("/soft")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<Void> userUnregisterAPI(
		@RequestBody UserWithdrawRequestDto userWithdrawRequest) {

		Integer userId = SecurityContextUtil.getUserOrStreamerId();

		userService.userWithdraw(userId, userWithdrawRequest);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// 마이페이지가 아닌 다른 유저의 정보 조회
	@GetMapping("/{userId}")
	public ResponseEntity<UserProfileResponseDto> userProfileFindAPI(
		@PathVariable(name = "userId") Integer userId){

		UserProfileResponseDto profileResponseDto = userService.userGetProfile(userId);

		return ResponseEntity.ok(profileResponseDto);
	}

	@GetMapping("/profile")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<MyProfileResponseDto> myPageGetAPI() {

		Integer userId = SecurityContextUtil.getUserOrStreamerId();

		MyProfileResponseDto myProfileResponse = userService.userGetMyProfile(userId);

		return ResponseEntity.ok(myProfileResponse);
	}

	@PatchMapping("/profile")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<UpdateMyProfileResponseDto> userProfileUpdateAPI(
		@RequestBody UpdateMyProfileRequestDto updateMyProfileRequest) {

		Integer userId = SecurityContextUtil.getUserOrStreamerId();

		UpdateMyProfileResponseDto updateMyProfileResponse = userService.updateMyProfile(userId, updateMyProfileRequest);

		return ResponseEntity.ok(updateMyProfileResponse);
	}

	@PutMapping("/password")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<UpdatePasswordResponseDto> userUpdatePasswordAPU(
		@RequestBody UpdatePasswordRequestDto updatePasswordRequest) {

		Integer userId = SecurityContextUtil.getUserOrStreamerId();

		UpdatePasswordResponseDto updatePasswordResponse = userService.updatePassword(userId, updatePasswordRequest);

		return ResponseEntity.ok(updatePasswordResponse);
	}
}

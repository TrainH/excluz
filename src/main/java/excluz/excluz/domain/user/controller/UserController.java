package excluz.excluz.domain.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

@Controller
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

//	추가: 회원가입 폼 보여주기
	@GetMapping("/signup")
	public String showSignupForm(){
		return "user/signup/form";
	}

	@PostMapping("/signup")
	public String userSignupAPI(
		@Valid UserSignupRequestDto signupRequest,
		Model model) {

		UserSignupResponseDto userSignupResponseDto = userService.userSignup(signupRequest);

		model.addAttribute("signupResponse", userSignupResponseDto);
		return "user/signup/success";
//		return ResponseEntity.ok(userSignupResponseDto);
	}

	@GetMapping("login")
	public String showLoginForm(){
		return "user/login/form";
	}


	@PostMapping("/login")
	public String userLoginForm(
			UserLoginRequestDto loginRequest,   // @RequestBody 대신 form 데이터 바인딩
			HttpServletResponse response        // 쿠키 설정 위해
	) {
		// ① 이메일/패스워드 확인 -> 토큰 생성
		UserLoginResponseDto userLoginResponseDto = userService.userLogin(loginRequest);
		// userLoginResponseDto.getToken()으로 꺼내도 되고,
		// 혹은 여기서 직접 jwtUtil.createToken(...) 해도 됨

		// ② createToken() 등에서 넘어온 JWT가 "Bearer ..." 형식일 것이므로 접두사를 제거
		String jwtToken = userLoginResponseDto.getToken();
		if (jwtToken.startsWith("Bearer ")) {
			jwtToken = jwtToken.substring(7); // "Bearer " 부분 잘라내기
		}

// ③ 순수 토큰을 쿠키에 저장
		Cookie tokenCookie = new Cookie("jwtToken", jwtToken);
		tokenCookie.setPath("/");
		tokenCookie.setHttpOnly(true);
		response.addCookie(tokenCookie);

// ④ 리다이렉트
		return "redirect:/api/v1/users/profile";

	}

	@GetMapping("/profile")
	@PreAuthorize("hasRole('CUSTOMER')") // 필요 시 권한 체크
	public String showProfile(Model model) {
		Integer userId = SecurityContextUtil.getUserOrStreamerId();

		MyProfileResponseDto myProfile = userService.userGetMyProfile(userId);

		model.addAttribute("myProfile", myProfile);

		return "user/profile/view";
	}

}

package excluz.excluz.domain.streamer.controller;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.streamer.dto.request.StreamerLoginRequestDto;
import excluz.excluz.domain.streamer.dto.response.StreamerLoginResponseDto;
import excluz.excluz.domain.streamer.dto.response.StreamerResponseDto;
import excluz.excluz.domain.streamer.dto.response.StreamerSummaryResponseDto;
import excluz.excluz.domain.streamer.service.StreamerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/streamers")
@RequiredArgsConstructor
@Slf4j
public class StreamerV1Controller {

	private final StreamerService streamerService;

	// 1) 로그인 폼 페이지
	@GetMapping("/login")
	public String showLoginForm() {
		return "streamer/login/form";
	}

	// 2) 로그인 처리 (Form Data)
	@PostMapping("/login")
	public String streamerLoginForm(
			@Valid StreamerLoginRequestDto loginRequestDto,
			HttpServletResponse response
	) {
		// Service 호출하여 JWT 토큰 발급
		log.info("test" + loginRequestDto.getEmail());
		log.info("test" + loginRequestDto.getPassword());
		StreamerLoginResponseDto streamerLoginResponseDto = streamerService.streamerLogin(loginRequestDto);
		String jwtToken = streamerLoginResponseDto.getToken();

		// "Bearer "가 포함되어 있다면 제거 후 쿠키에 저장 (일반적으로 'Bearer ' 없이 저장)
		if (jwtToken.startsWith("Bearer ")) {
			jwtToken = jwtToken.substring(7);
		}

		Cookie tokenCookie = new Cookie("jwtToken", jwtToken);
		tokenCookie.setPath("/");
		tokenCookie.setHttpOnly(true);
		response.addCookie(tokenCookie);

		// 로그인 성공 시 스트리머 마이페이지로 리다이렉트
		return "redirect:/api/v1/streamers/my-page";
	}

	// 3) 스트리머 본인 조회 (마이페이지)
	@GetMapping("/my-page")
	@PreAuthorize("hasRole('STREAMER')")
	public String getPersonalInfo(Model model) {
		Integer streamerId = SecurityContextUtil.getUserOrStreamerId();
		StreamerResponseDto responseDto = streamerService.getPersonalInfo(streamerId);

		model.addAttribute("streamerInfo", responseDto);
		return "streamer/mypage/view";
	}

	// 4) 특정 스트리머 조회
	@GetMapping("/{streamerId}")
	public String getStreamer(
			@PathVariable Integer streamerId,
			Model model
	) {
		StreamerSummaryResponseDto responseDto = streamerService.getStreamer(streamerId);
		model.addAttribute("streamerSummary", responseDto);
		return "streamer/profile/summary";
	}
}
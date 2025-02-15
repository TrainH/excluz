package excluz.excluz.domain.streamer.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import excluz.excluz.domain.streamer.dto.request.*;
import excluz.excluz.domain.streamer.dto.response.*;
import excluz.excluz.domain.streamer.service.StreamerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/streamers")
@RequiredArgsConstructor
public class StreamerV1Controller {

	private final StreamerService streamerService;

	@PostMapping("/signup")
	public ResponseEntity<Void> streamerSignup(@Valid @RequestBody StreamerSignupRequestDto signupRequestDto) {

		streamerService.streamerSignup(signupRequestDto);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<StreamerLoginResponseDto> streamerLogin(@Valid @RequestBody StreamerLoginRequestDto loginRequestDto) {

		return new ResponseEntity<>(streamerService.streamerLogin(loginRequestDto), HttpStatus.OK);
	}

	@DeleteMapping("/{streamerId}")
	@PreAuthorize("hasRole('STREAMER')")
	public ResponseEntity<Void> deleteStreamer(
		@PathVariable Integer streamerId,
		@RequestBody StreamerDeleteRequestDto deleteRequestDto
	) {
		streamerService.deleteStreamer(streamerId, deleteRequestDto.getPassword());

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PatchMapping()
	@PreAuthorize("hasRole('STREAMER')")
	public ResponseEntity<StreamerResponseDto> updateStreamer(
		@AuthenticationPrincipal User user,
		@RequestBody StreamerUpdateRequestDto requestDto
	) {
		Integer streamerId = Integer.valueOf(user.getUsername());

		StreamerResponseDto responseDto = streamerService.updateStreamer(streamerId, requestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	// 스트리머 본인 조회
	@GetMapping("/my-page")
	@PreAuthorize("hasRole('STREAMER')")
	public ResponseEntity<StreamerResponseDto> getPersonalInfo(
		@AuthenticationPrincipal User user
	) {
		Integer streamerId = Integer.valueOf(user.getUsername());

		StreamerResponseDto responseDto = streamerService.getPersonalInfo(streamerId);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@GetMapping()
	public ResponseEntity<Page<StreamerSummaryResponseDto>> getStreamerList(
		@RequestParam(required = false) String nickName,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Page<StreamerSummaryResponseDto> responseDtoList = streamerService.getStreamerList(page, size, nickName);

		return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
	}
}

package excluz.excluz.domain.streamer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.domain.streamer.dto.request.StreamerSignupRequestDto;
import excluz.excluz.domain.streamer.service.StreamerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/streamers")
@RequiredArgsConstructor
public class StreamerV1Controller {

	private final StreamerService streamerService;

	@PostMapping("/signup")
	public ResponseEntity<Void> streamerSignup(@Valid @RequestBody StreamerSignupRequestDto signupRequestDto){

		streamerService.streamerSignup(signupRequestDto);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}

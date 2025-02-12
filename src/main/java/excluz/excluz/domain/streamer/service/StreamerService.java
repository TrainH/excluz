package excluz.excluz.domain.streamer.service;

import org.springframework.stereotype.Service;

import excluz.excluz.domain.streamer.dto.request.StreamerSignupRequestDto;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StreamerService {

	private final StreamerRepository streamerRepository;

	public void streamerSignup(@Valid StreamerSignupRequestDto signupRequestDto) {

	}
}

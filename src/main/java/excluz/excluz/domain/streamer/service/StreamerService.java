package excluz.excluz.domain.streamer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.auth.util.PasswordEncoder;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.streamer.dto.request.StreamerSignupRequestDto;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StreamerService {

	private final StreamerRepository streamerRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void streamerSignup(StreamerSignupRequestDto signupRequestDto) {
		if(!signupRequestDto.getPassword().equals(signupRequestDto.getReEnterPassword())){
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

		Streamer streamer= Streamer.builder()
			.name(signupRequestDto.getName())
			.nickName(signupRequestDto.getNickName())
			.phoneNumber(signupRequestDto.getPhoneNumber())
			.email(signupRequestDto.getEmail())
			.password(encodedPassword)
			.build();

		streamerRepository.save(streamer);
	}
}

package excluz.excluz.domain.streamer.service;

import org.springframework.stereotype.Service;

import excluz.excluz.common.entity.Streamer;
import excluz.excluz.domain.streamer.dto.request.StreamerSignupRequestDto;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StreamerService {

	private final StreamerRepository streamerRepository;

	public void streamerSignup(StreamerSignupRequestDto signupRequestDto) {
		if(signupRequestDto.getPassword().equals(signupRequestDto.getReEnterPassword())){
			throw new RuntimeException(); /*TODO: 예외처리 수정하기*/
		}

		String encodedPassword = PasswordEncoder.encode(signupRequestDto.getPassword());

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

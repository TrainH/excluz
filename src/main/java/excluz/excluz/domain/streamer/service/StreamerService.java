package excluz.excluz.domain.streamer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.Streamer;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.streamer.dto.request.StreamerLoginRequestDto;
import excluz.excluz.domain.streamer.dto.request.StreamerSignupRequestDto;
import excluz.excluz.domain.streamer.dto.response.StreamerLoginResponseDto;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StreamerService {

	private final StreamerRepository streamerRepository;

	@Transactional
	public void streamerSignup(StreamerSignupRequestDto signupRequestDto) {
		if(!signupRequestDto.getPassword().equals(signupRequestDto.getReEnterPassword())){
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
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

	public StreamerLoginResponseDto streamerLogin(StreamerLoginRequestDto loginRequestDto) {
		Streamer streamer = findStreamerByEmail(loginRequestDto);

		if(!PasswordEncoder.matches(loginRequestDto.getPassword(), streamer.getPassword())){
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		/*TODO: JWT 토큰 양식에 맞게 수정하기*/
		String bearerToken = jwtUtil.createToken(streamer.getId(), streamer.getEmail(), streamer.getNickName(), streamer.getUserRole());

		return StreamerLoginResponseDto.from(bearerToken);
	}

	/* 기타 메서드 */
	private Streamer findStreamerByEmail(StreamerLoginRequestDto loginRequestDto) {
		return streamerRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(
			() -> new NotFoundException(ErrorCode.UNAUTHORIZED_USER)
		);
	}
}

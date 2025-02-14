package excluz.excluz.domain.streamer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.auth.util.PasswordEncoder;
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
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void streamerSignup(StreamerSignupRequestDto signupRequestDto) {
		if(!signupRequestDto.getPassword().equals(signupRequestDto.getReEnterPassword())){
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

		Streamer streamer = Streamer.builder()
			.name(signupRequestDto.getName())
			.nickName(signupRequestDto.getNickName())
			.phoneNumber(signupRequestDto.getPhoneNumber())
			.email(signupRequestDto.getEmail())
			.password(encodedPassword)
			.build();

		streamerRepository.save(streamer);
	}

	public StreamerLoginResponseDto streamerLogin(StreamerLoginRequestDto loginRequestDto) {
		Streamer streamer = streamerRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(
			() -> new NotFoundException(ErrorCode.UNAUTHORIZED_USER)
		);

		if(!passwordEncoder.matches(loginRequestDto.getPassword(), streamer.getPassword())){
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		/*TODO: JWT 토큰 양식에 맞게 수정하기*/
		String bearerToken = "jwtUtil.createToken(streamer.getId(), streamer.getEmail(), streamer.getNickName(),streamer.getUserRole())";

		return StreamerLoginResponseDto.from(bearerToken);
	}

	@Transactional
	public void deleteStreamer(Integer streamerId, String password) {
		Streamer streamer = findStreamerById(streamerId);

		if (!passwordEncoder.matches(password, streamer.getPassword())) {
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		// 소프트 딜리트
		streamer.updateStreamerStatus(true);
	}

	/* 기타 메서드 */
	public Streamer findStreamerById(Integer streamerId) {
		return streamerRepository.findById(streamerId).orElseThrow(
			() -> new NotFoundException(ErrorCode.UNAUTHORIZED_USER)
		);
	}
}

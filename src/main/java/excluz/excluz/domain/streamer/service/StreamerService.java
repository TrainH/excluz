package excluz.excluz.domain.streamer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.auth.util.JwtUtil;
import excluz.excluz.common.entity.EmailVerify;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.emailVerification.repository.EmailVerifyRepository;
import excluz.excluz.domain.streamer.dto.request.StreamerLoginRequestDto;
import excluz.excluz.domain.streamer.dto.request.StreamerSignupRequestDto;
import excluz.excluz.domain.streamer.dto.request.StreamerUpdateRequestDto;
import excluz.excluz.domain.streamer.dto.response.StreamerLoginResponseDto;
import excluz.excluz.domain.streamer.dto.response.StreamerResponseDto;
import excluz.excluz.domain.streamer.dto.response.StreamerSummaryResponseDto;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StreamerService {

	private final StreamerRepository streamerRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailVerifyRepository emailVerifyRepository;
	private final JwtUtil jwtUtil;

	@Transactional
	public StreamerResponseDto streamerSignup(StreamerSignupRequestDto signupRequestDto) {
		if (!signupRequestDto.getPassword().equals(signupRequestDto.getReEnterPassword())) {
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		EmailVerify emailVerify = emailVerifyRepository
			.findByEmail(signupRequestDto.getEmail())
			.orElseThrow(() -> new BadRequestException(ErrorCode.EMAIL_VERIFICATION_NOT_REQUESTED));

		if (!emailVerify.getIsVerified()) {
			throw new BadRequestException(ErrorCode.EMAIL_VERIFICATION_NOT_COMPLETED);
		}

		String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

		Streamer streamer = Streamer.builder()
			.name(signupRequestDto.getName())
			.nickName(signupRequestDto.getNickName())
			.phoneNumber(signupRequestDto.getPhoneNumber())
			.email(signupRequestDto.getEmail())
			.password(encodedPassword)
			.build();

		return StreamerResponseDto.from(streamerRepository.save(streamer));
	}

	public StreamerLoginResponseDto streamerLogin(StreamerLoginRequestDto loginRequestDto) {
		Streamer streamer = streamerRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(
			() -> new NotFoundException(ErrorCode.UNAUTHORIZED_USER)
		);

		if (!passwordEncoder.matches(loginRequestDto.getPassword(), streamer.getPassword())) {
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		String bearerToken = jwtUtil.createToken(streamer.getEmail(), streamer.getId(), streamer.getUserRole());

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

	@Transactional
	public StreamerResponseDto updateStreamer(Integer streamerId, StreamerUpdateRequestDto requestDto) {
		Streamer streamer = findStreamerById(streamerId);

		if (streamer.isDeleted()) {
			throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
		}

		streamer.updateStreamer(
			requestDto.getName(),
			requestDto.getNickName(),
			requestDto.getPhoneNumber(),
			requestDto.getEmail());

		return StreamerResponseDto.from(streamer);
	}

	@Transactional(readOnly = true)
	public StreamerResponseDto getPersonalInfo(Integer streamerId) {
		Streamer streamer = findStreamerById(streamerId);

		if (streamer.isDeleted()) {
			throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
		}

		return StreamerResponseDto.from(streamer);
	}

	@Transactional(readOnly = true)
	public Page<StreamerSummaryResponseDto> getStreamerList(int page, int size, String nickName) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), size);

		Page<Streamer> streamerList = streamerRepository.findByNickName(pageable, nickName);

		return streamerList.map(StreamerSummaryResponseDto::from);
	}

	@Transactional(readOnly = true)
	public StreamerSummaryResponseDto getStreamer(Integer streamerId) {
		Streamer streamer = streamerRepository.findById(streamerId).orElseThrow(
			() -> new NotFoundException(ErrorCode.USER_NOT_FOUND)
		);

		if (streamer.isDeleted()) {
			throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
		}

		return StreamerSummaryResponseDto.from(streamer);
	}

	/* 기타 메서드 */
	public Streamer findStreamerById(Integer streamerId) {
		return streamerRepository.findById(streamerId).orElseThrow(
			() -> new NotFoundException(ErrorCode.UNAUTHORIZED_USER)
		);
	}
}

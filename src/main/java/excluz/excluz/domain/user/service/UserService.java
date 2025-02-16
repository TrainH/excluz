package excluz.excluz.domain.user.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.auth.util.JwtUtil;
import excluz.excluz.common.entity.User;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.user.dto.request.UserLoginRequestDto;
import excluz.excluz.domain.user.dto.request.UserSignupRequestDto;
import excluz.excluz.domain.user.dto.request.UserWithdrawRequestDto;
import excluz.excluz.domain.user.dto.response.UserLoginResponseDto;
import excluz.excluz.domain.user.dto.response.UserProfileResponseDto;
import excluz.excluz.domain.user.dto.response.UserSignupResponseDto;
import excluz.excluz.domain.user.dto.response.UserWithdrawResponseDto;
import excluz.excluz.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	// 유저 회원가입
	public UserSignupResponseDto userSignup(UserSignupRequestDto signupRequest) {

		// 가입된 유저의 이메일 여부를 확인
		Optional<User> existingUser = userRepository.findByEmail(signupRequest.getEmail());

		// 이미 가입된 유저의 경우의 예외
		if (existingUser.isPresent()) {
			throw new BadRequestException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}

		// 리퀘스트 요청에 들어온 비밀번호와 재확인 비밀번호가 일치 하지 않을 시 예외
		if (!signupRequest.getPassword().equals(signupRequest.getReEnterPassword())) {
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		// 비밀번호 해싱 처리
		String bcryptPassword = passwordEncoder.encode(signupRequest.getPassword());

		User user = new User(
			signupRequest.getName(),
			signupRequest.getNickName(),
			signupRequest.getPhoneNumber(),
			signupRequest.getAddress(),
			signupRequest.getEmail(),
			bcryptPassword);

		userRepository.save(user);

		return new UserSignupResponseDto("회원가입이 완료되었습니다.", user);
	}

	// 유저 로그인
	public UserLoginResponseDto userLogin(UserLoginRequestDto loginRequest) {

		// 유저 정보를 이메일로 찾고 유저 정보를 찾을 수 없다면 예외 처리
		User user = userRepository.findByEmail(loginRequest.getEmail())
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

		// 비밀번호 검증 로직
		if(!passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())) {
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		// 토큰 생성 로직
		String token = jwtUtil.createToken(user.getEmail(), user.getId(), user.getUserRole());

		// 로그인 완료 메세지 및 토큰값 발행
		return new UserLoginResponseDto("로그인 되었습니다.", token);
	}

	// 회원탈퇴
	@Transactional
	public UserWithdrawResponseDto userWithdraw(
		Integer userId, UserWithdrawRequestDto userWithdrawRequestDto) {

		// 유저 정보를 userId 로 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

		// 리퀘스트 요청에 들어온 비밀번호와 재확인 비밀번호가 일치 하지 않을 시 예외
		if (!userWithdrawRequestDto.getPassword().equals(userWithdrawRequestDto.getReEnterPassword())) {
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		// 비밀번호 검증 로직
		if(!passwordEncoder.matches(userWithdrawRequestDto.getPassword(), user.getPassword())) {
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		// 유저의 isDelete의 상태가 true 가 됨
		user.updateUserStatus(true);

		return new UserWithdrawResponseDto("회원탈퇴가 완료되었습니다 저희 서비스를 이용해주셔서 감사합니다.");
	}

	// 유저 조회
	@Transactional(readOnly = true)
	public UserProfileResponseDto getProfile(Integer userId) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

		// 탈퇴한 회원 조회시 예외처리
		if (user.getIsDeleted()) {
			throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
		}

		return new UserProfileResponseDto(user);
	}

}

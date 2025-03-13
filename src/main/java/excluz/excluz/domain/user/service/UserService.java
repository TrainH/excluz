package excluz.excluz.domain.user.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.auth.util.JwtUtil;
import excluz.excluz.common.entity.EmailVerify;
import excluz.excluz.common.entity.User;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.emailVerification.repository.EmailVerifyRepository;
import excluz.excluz.domain.user.dto.request.UpdateMyProfileRequestDto;
import excluz.excluz.domain.user.dto.request.UpdatePasswordRequestDto;
import excluz.excluz.domain.user.dto.request.UserLoginRequestDto;
import excluz.excluz.domain.user.dto.request.UserSignupRequestDto;
import excluz.excluz.domain.user.dto.request.UserWithdrawRequestDto;
import excluz.excluz.domain.user.dto.response.MyProfileResponseDto;
import excluz.excluz.domain.user.dto.response.UpdateMyProfileResponseDto;
import excluz.excluz.domain.user.dto.response.UpdatePasswordResponseDto;
import excluz.excluz.domain.user.dto.response.UserLoginResponseDto;
import excluz.excluz.domain.user.dto.response.UserProfileResponseDto;
import excluz.excluz.domain.user.dto.response.UserSignupResponseDto;
import excluz.excluz.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final EmailVerifyRepository emailVerifyRepository;

	// 유저 회원가입
	public UserSignupResponseDto userSignup(UserSignupRequestDto signupRequest) {
		if (userRepository.findByNickName(signupRequest.getNickName()).isPresent()) {
			throw new BadRequestException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}

		if (userRepository.findByPhoneNumber(signupRequest.getPhoneNumber()).isPresent()) {
			throw new BadRequestException(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS);
		}

		// 가입된 유저의 이메일 여부를 확인 (비즈니스 규칙: 탈퇴한 이메일로 재가입 불가능)
		Optional<User> existingUser = userRepository.findByEmail(signupRequest.getEmail());

		// Oauth 소셜 로그인을 진행했을때 로그인된 이메일과 소셜 로그인 연동을 처리한다. 소셜 로그인이 연동 되어있는 경우에만 메일 발송

		// 이미 가입된 유저의 경우의 예외
		if (existingUser.isPresent()) {
			throw new BadRequestException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}

		EmailVerify emailVerify = emailVerifyRepository
			.findByEmail(signupRequest.getEmail())
			.orElseThrow(() -> new BadRequestException(ErrorCode.EMAIL_VERIFICATION_NOT_REQUESTED));

		if (!emailVerify.getIsVerified()) {
			throw new BadRequestException(ErrorCode.EMAIL_VERIFICATION_NOT_COMPLETED);
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

		return new UserSignupResponseDto(user);
	}

	// 유저 로그인
	public UserLoginResponseDto userLogin(UserLoginRequestDto loginRequest) {

		// 유저 정보를 이메일로 찾고 유저 정보를 찾을 수 없다면 예외 처리
		User user = userRepository.findByEmail(loginRequest.getEmail())
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

		// 비밀번호 검증 로직
		validatePassword(loginRequest.getPassword(),user.getPassword());

		// 토큰 생성 로직
		String token = jwtUtil.createToken(user.getEmail(), user.getId(), user.getUserRole());

		// 로그인 완료 메세지 및 토큰값 발행
		return new UserLoginResponseDto(token);
	}

	// 회원탈퇴
	@Transactional
	public void userWithdraw(Integer userId, UserWithdrawRequestDto userWithdrawRequest) {

		// 유저 정보를 userId 로 조회
		User user = userProfile(userId);

		// 비밀번호 검증 로직
		validatePassword(userWithdrawRequest.getPassword(), user.getPassword());

		// 유저의 isDeleted 의 상태가 true 가 됨
		user.updateUserStatus(true);
	}

	// 유저 조회
	@Transactional(readOnly = true)
	public UserProfileResponseDto userGetProfile(Integer userId) {

		User user = userProfile(userId);

		// 탈퇴한 회원 조회시 예외처리
		if (user.getIsDeleted()) {
			throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
		}

		return new UserProfileResponseDto(user);
	}

	// 마이페이지
	@Transactional(readOnly = true)
	public MyProfileResponseDto userGetMyProfile(Integer userId) {

		User user = userProfile(userId);

		return new MyProfileResponseDto(user);
	}

	// 내정보 수정
	@Transactional
	public UpdateMyProfileResponseDto updateMyProfile(Integer userId, UpdateMyProfileRequestDto updateMyProfileRequest) {

		User user = userProfile(userId);

		// 비밀번호 검증로직
		if (!user.getOauthUser()) { validatePassword(updateMyProfileRequest.getPassword(), user.getPassword()); }

		user.updateUserProfile(
			updateMyProfileRequest.getNickName(),
			updateMyProfileRequest.getPhoneNumber(),
			updateMyProfileRequest.getAddress()
			);

		return new UpdateMyProfileResponseDto(user);
	}

	// 비밀번호 변경 로직
	@Transactional
	public UpdatePasswordResponseDto updatePassword(Integer userId, UpdatePasswordRequestDto updatePasswordRequest) {

		User user = userProfile(userId);

		// 비밀번호 검증 로직
		if (user.getOauthUser()) {
			throw new BadRequestException(ErrorCode.OAUTH_USER_CANT_CHANGE_PASSWORD);
		} else {
			validatePassword(updatePasswordRequest.getOldPassword(), user.getPassword());
		}

		// 새로운 비밀번호가 이전에 사용하던 비밀번호와 같을 경우 예외처리
		if(passwordEncoder.matches(updatePasswordRequest.getNewPassword(), user.getPassword())) {
			throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
		}

		// 새로운 비밀번호 해싱처리
		String bcryptPassword = passwordEncoder.encode(updatePasswordRequest.getNewPassword());

		user.updatePassword(bcryptPassword);

		return new UpdatePasswordResponseDto(user);
	}

	// Oauth 2.0 회원가입
	@Transactional
	public User OauthLogin(String email, String name) {
		if (email == null) {
			throw new BadRequestException(ErrorCode.EMAIL_NULL);
		}
		Optional<User> existingUser = userRepository.findByEmail(email);

		if (existingUser.isPresent()) {
			return existingUser.get();
		} else {
			String randomUUID = passwordEncoder.encode(UUID.randomUUID().toString());
			String createNickname = email.split("@")[0];

			User user = User.builder()
				.name(name)
				.nickName(createNickname)
				.phoneNumber("")
				.email(email)
				.address("")
				.password(randomUUID)
				.build();

			user.updateUserStatus(true);

			return userRepository.save(user);
		}
	}

	// 기타 메서드(비밀번호 검증)
	public void validatePassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}
	}

	// 기타 메서드 (회원 정보 조회)
	public User userProfile(Integer userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
	}


}

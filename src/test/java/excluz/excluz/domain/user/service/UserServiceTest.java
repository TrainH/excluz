package excluz.excluz.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import excluz.excluz.auth.util.JwtUtil;
import excluz.excluz.common.datas.SharedData;
import excluz.excluz.common.entity.User;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.domain.user.dto.request.UpdateMyProfileRequestDto;
import excluz.excluz.domain.user.dto.request.UpdatePasswordRequestDto;
import excluz.excluz.domain.user.dto.request.UserLoginRequestDto;
import excluz.excluz.domain.user.dto.request.UserSignupRequestDto;
import excluz.excluz.domain.user.dto.request.UserWithdrawRequestDto;
import excluz.excluz.domain.user.dto.response.MyProfileResponseDto;
import excluz.excluz.domain.user.dto.response.UpdateMyProfileResponseDto;
import excluz.excluz.domain.user.dto.response.UserLoginResponseDto;
import excluz.excluz.domain.user.dto.response.UserProfileResponseDto;
import excluz.excluz.domain.user.dto.response.UserSignupResponseDto;
import excluz.excluz.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	// 가짜 객체
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	// 실제 객체
	@InjectMocks
	private UserService userService;

	@Test
	@DisplayName("회원 가입 - 성공 케이스")
	public void userSignup() {
		// give
		UserSignupRequestDto signupRequest = SharedData.USER_SIGNUP_REQUEST_DTO;

		// 실제로 동작할건 서비스 만 작동 시킬것이기 때문에 가짜 데이터를 넣어줌
		when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("&@a2djksladsaksllasrwpookl");

		// when
		UserSignupResponseDto userSignupResponse = userService.userSignup(signupRequest);

		// then
		assertEquals(signupRequest.getName(), userSignupResponse.getName());
		assertEquals(signupRequest.getNickName(), userSignupResponse.getNickName());
		assertEquals(signupRequest.getPhoneNumber(), userSignupResponse.getPhoneNumber());
		assertEquals(signupRequest.getAddress(), userSignupResponse.getAddress());
		assertEquals(signupRequest.getEmail(), userSignupResponse.getEmail());

		// 실제로 코드가 실행됐는지 확인하는 로직 (PasswordEncoder)
		verify(userRepository).findByEmail(signupRequest.getEmail());
		verify(passwordEncoder).encode(signupRequest.getPassword());
	}

	@Test
	@DisplayName("회원 가입 - 실패 케이스(이미 가입된 회원)")
	public void userSignup_BadRequestException() {
		// give
		UserSignupRequestDto signupRequest = SharedData.USER_SIGNUP_REQUEST_DTO;
		User user = mock(User.class);

		when(userRepository.findByEmail(signupRequest.getEmail())).thenReturn(Optional.of(user));

		// when, then
		Assertions.assertThrows(BadRequestException.class, () -> {
			userService.userSignup(signupRequest);
		});
	}

	@Test
	@DisplayName("로그인 - 성공 케이스")
	public void userLogin() {
		//give
		UserLoginRequestDto loginRequest = new UserLoginRequestDto(
			SharedData.USER_EMAIL1, SharedData.USER_PASSWORD1);

		User user = User.builder()
			.email(SharedData.USER_EMAIL1)
				.build();

		String token = "token";

		// when
		when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
		when(jwtUtil.createToken(user.getEmail(), user.getId(), user.getUserRole())).thenReturn(token);

		UserLoginResponseDto userLoginResponse = userService.userLogin(loginRequest);

		// dto의 반환 값이 null이 아닌지 검증 하는 로직이 필요
		assertNotNull(userLoginResponse);

		// 토큰값이 null 인지 혹은 empty String 인지 검증 필요
		assertNotNull(userLoginResponse.getToken());
		assertFalse(userLoginResponse.getToken().isEmpty());
		assertEquals(userLoginResponse.getToken(), token);

		// then
		verify(userRepository).findByEmail(loginRequest.getEmail());
		verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
		verify(jwtUtil).createToken(user.getEmail(), user.getId(), user.getUserRole());
	}

	@Test
	@DisplayName("로그인 - 실패 케이스")
	public void userLoginFailed() {
		// give
		UserLoginRequestDto LoginRequest = new UserLoginRequestDto(SharedData.USER_EMAIL1, SharedData.USER_PASSWORD1);

		when(userRepository.findByEmail(LoginRequest.getEmail())).thenReturn(Optional.empty());

		// when
		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.userLogin(LoginRequest);
		});
	}

	@Test
	@DisplayName("회원탈퇴 - 성공 케이스")
	public void userWithdraw() {
		// give
		UserWithdrawRequestDto withdrawRequest = new UserWithdrawRequestDto(SharedData.USER_PASSWORD1, SharedData.USER_REENTER_PASSWORD1);

		User user = User.builder()
				.build();

		// user Entity의 생성자에 userId값이 존재하지 않으므로 id값을 강제로 설정해 줌
		ReflectionTestUtils.setField(user, "id", SharedData.USER_ID);

		when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(withdrawRequest.getPassword(), user.getPassword())).thenReturn(true);

		// when
		userService.userWithdraw(user.getId(), withdrawRequest);

		//then
		assertThat(user.getIsDeleted()).isEqualTo(true);

		verify(userRepository).findById(user.getId());
		verify(passwordEncoder).matches(withdrawRequest.getPassword(), user.getPassword());
	}

	@Test
	@DisplayName("회원조회 - 성공 케이스")
	public void userGetProfile() {
		//give
		User user = User.builder()
			.email(SharedData.USER_EMAIL1)
			.nickName(SharedData.USER_NICKNAME1)
			.build();

		ReflectionTestUtils.setField(user, "id", SharedData.USER_ID);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		//when
		UserProfileResponseDto userProfileResponse = userService.userGetProfile(user.getId());

		//then
		assertEquals(user.getNickName(), userProfileResponse.getNickName());
		assertEquals(user.getEmail(), userProfileResponse.getEmail());

		verify(userRepository).findById(user.getId());
	}

	@Test
	@DisplayName("회원조회 - 실패 케이스")
	public void userGetProfile_NotFoundException() {
		// give
		User user = User.builder()
			.build();

		ReflectionTestUtils.setField(user, "id", SharedData.USER_ID);
		ReflectionTestUtils.setField(user, "isDeleted", true);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		// then
		Assertions.assertThrows(NotFoundException.class, () ->
			userService.userGetProfile(user.getId()));
	}

	@Test
	@DisplayName("마이페이지 - 성공 케이스")
	public void userGetMyProfile() {
		// give
		User user = User.builder()
			.name(SharedData.USER_NAME1)
			.nickName(SharedData.USER_NICKNAME1)
			.email(SharedData.USER_EMAIL1)
			.phoneNumber(SharedData.USER_PHONE_NUMBER1)
			.address(SharedData.ADDRESS1)
			.build();

		ReflectionTestUtils.setField(user, "id", SharedData.USER_ID);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		// when
		MyProfileResponseDto myProfileResponse = userService.userGetMyProfile(user.getId());

		// then
		assertEquals(user.getName(), myProfileResponse.getName());
		assertEquals(user.getNickName(), myProfileResponse.getNickName());
		assertEquals(user.getEmail(), myProfileResponse.getEmail());
		assertEquals(user.getPhoneNumber(), myProfileResponse.getPhoneNumber());
		assertEquals(user.getAddress(), myProfileResponse.getAddress());

		verify(userRepository).findById(user.getId());
	}

	@Test
	@DisplayName("내정보 수정 - 성공 케이스")
	public void UpdateMyProfile() {
		// give
		UpdateMyProfileRequestDto updateMyProfileRequest = new UpdateMyProfileRequestDto(
			SharedData.UPDATE_NICKNAME2, SharedData.USER_PHONE_NUMBER2, SharedData.USER_ADDRESS2, SharedData.USER_PASSWORD1);

		User user = User.builder()
			.nickName(SharedData.USER_NICKNAME1)
			.phoneNumber(SharedData.USER_PHONE_NUMBER1)
			.address(SharedData.ADDRESS1)
			.build();

		ReflectionTestUtils.setField(user, "id", SharedData.USER_ID);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(updateMyProfileRequest.getPassword(), user.getPassword())).thenReturn(true);

		// when
		UpdateMyProfileResponseDto updateMyProfileResponse = userService.updateMyProfile(user.getId(), updateMyProfileRequest);

		assertEquals(user.getNickName(), updateMyProfileResponse.getNickname());
		assertEquals(user.getPhoneNumber(), updateMyProfileResponse.getPhoneNumber());
		assertEquals(user.getAddress(), updateMyProfileResponse.getAddress());

		verify(userRepository).findById(user.getId());
	}

	@Test
	@DisplayName("내정보 수정 - 실패 케이스")
	public void updateMyProfile_BadRequestException() {
		UpdateMyProfileRequestDto updateMyProfileRequestDto = new UpdateMyProfileRequestDto(
			SharedData.UPDATE_NICKNAME2, SharedData.USER_PHONE_NUMBER2, SharedData.USER_ADDRESS2, SharedData.USER_PASSWORD2);

		User user = User.builder()
			.build();

		ReflectionTestUtils.setField(user, "id", SharedData.USER_ID);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		Assertions.assertThrows(BadRequestException.class, () ->
			userService.updateMyProfile(user.getId(), updateMyProfileRequestDto));
	}

	// 5분기록 보드 작성할 것
	@Test
	@DisplayName("비밀번호 변경 - 성공 케이스")
	public void updatePassword() {
		//give
		UpdatePasswordRequestDto updatePasswordRequest = new UpdatePasswordRequestDto(
			SharedData.USER_PASSWORD1, SharedData.USER_PASSWORD2, SharedData.USER_REENTER_PASSWORD2);

		User user = User.builder()
			.build();

		ReflectionTestUtils.setField(user, "id", SharedData.USER_ID);
		ReflectionTestUtils.setField(user, "password", SharedData.USER_PASSWORD1);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		// System.out.println("유저 비밀번호: " + user.getPassword());
		when(passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())).thenReturn(true);
		when(passwordEncoder.matches(updatePasswordRequest.getNewPassword(), user.getPassword())).thenReturn(false);
		when(passwordEncoder.encode(updatePasswordRequest.getNewPassword())).thenReturn(updatePasswordRequest.getNewPassword());

		// when
		userService.updatePassword(user.getId(), updatePasswordRequest);

		//then
		assertEquals(updatePasswordRequest.getNewPassword(), user.getPassword());
		// System.out.println("유저 비밀번호: " + user.getPassword());

		verify(userRepository).findById(user.getId());
		verify(passwordEncoder).matches(updatePasswordRequest.getOldPassword(), SharedData.USER_PASSWORD1);
		verify(passwordEncoder).matches(updatePasswordRequest.getNewPassword(), SharedData.USER_PASSWORD1);
	}

	@Test
	@DisplayName("비밀번호 변경 - 실패 케이스")
	public void updatePassword_BadRequestException() {
		// give
		UpdatePasswordRequestDto updatePasswordRequest = new UpdatePasswordRequestDto(
			SharedData.USER_PASSWORD1, SharedData.USER_PASSWORD1, SharedData.USER_PASSWORD1
		);

		User user = User.builder()
			.build();

		ReflectionTestUtils.setField(user, "id", SharedData.USER_ID);
		ReflectionTestUtils.setField(user, "password", SharedData.USER_PASSWORD1);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(updatePasswordRequest.getNewPassword(), user.getPassword())).thenReturn(true);

		Assertions.assertThrows(BadRequestException.class, () ->
			userService.updatePassword(user.getId(), updatePasswordRequest));
	}
}

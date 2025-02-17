package excluz.excluz.domain.streamer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import excluz.excluz.auth.util.JwtUtil;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.domain.streamer.dto.request.StreamerLoginRequestDto;
import excluz.excluz.domain.streamer.dto.request.StreamerSignupRequestDto;
import excluz.excluz.domain.streamer.dto.response.StreamerLoginResponseDto;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import excluz.excluz.domain.user.enums.UserRole;

@ExtendWith(MockitoExtension.class)
class StreamerServiceTest {

	/* 공유 데이터 */
	// Streamer 데이터
	public final static Integer TEST_STREAMER_ID1 = 1;
	public final static String TEST_STREAMER_NAME1 = "홍길동";
	public final static String TEST_STREAMER_NICKNAME1 = "암행어사";
	public final static String TEST_STREAMER_PHONE_NUMBER1 = "010-1234-1234";
	public final static String TEST_STREAMER_EMAIL1 = "test12@test.com";
	public final static String TEST_STREAMER_PASSWORD1 = "Qwer1234!!!!";
	public final static String TEST_STREAMER_REENTER_PASSWORD1 = "Qwer1234!!!!";
	public final static Streamer TEST_STREAMER1 = new Streamer(TEST_STREAMER_NAME1, TEST_STREAMER_NICKNAME1, TEST_STREAMER_PHONE_NUMBER1, TEST_STREAMER_EMAIL1, TEST_STREAMER_PASSWORD1);
	public final static StreamerSignupRequestDto TEST_STREAMER_SIGNUP_REQUEST_DTO = new StreamerSignupRequestDto(TEST_STREAMER_NAME1, TEST_STREAMER_NICKNAME1, TEST_STREAMER_PHONE_NUMBER1, TEST_STREAMER_EMAIL1, TEST_STREAMER_PASSWORD1, TEST_STREAMER_REENTER_PASSWORD1);
	public final static StreamerLoginRequestDto TEST_STREAMER_LOGIN_REQUEST_DTO = new StreamerLoginRequestDto(TEST_STREAMER_EMAIL1, TEST_STREAMER_PASSWORD1);

	@InjectMocks
	StreamerService streamerService;

	@Mock
	StreamerRepository streamerRepository;
	@Mock
	PasswordEncoder passwordEncoder;
	@Mock
	JwtUtil jwtUtil;

	private static MockedStatic<StreamerLoginResponseDto> mockedStatic;

	@BeforeAll
	public static void beforeAl1() {
		mockedStatic = mockStatic(StreamerLoginResponseDto.class);
	}

	@AfterAll
	public static void afterAl1() {
		mockedStatic.close();
	}

	@Test
	@DisplayName("success: 스트리머 회원가입 성공")
	void streamerSignup() {
		// given
		StreamerSignupRequestDto signupRequestDto = TEST_STREAMER_SIGNUP_REQUEST_DTO;

		// when
		streamerService.streamerSignup(signupRequestDto);

		// then
		verify(streamerRepository, times(1)).save(any(Streamer.class));
	}

	@Test
	@DisplayName("success: 스트리머 토큰 생성 성공")
	void streamerLogin() {
		// given
		StreamerLoginRequestDto loginRequestDto = TEST_STREAMER_LOGIN_REQUEST_DTO;
		String bearerToken = "Bearer ";
		StreamerLoginResponseDto loginResponseDto = new StreamerLoginResponseDto(bearerToken);
		when(streamerRepository.findByEmail(anyString())).thenReturn(Optional.of(TEST_STREAMER1));
		when(passwordEncoder.matches(anyString(),anyString())).thenReturn(true);
		when(jwtUtil.createToken(anyString(),any(),any(UserRole.class))).thenReturn(bearerToken);
		given(StreamerLoginResponseDto.from(anyString())).willReturn(loginResponseDto);

		// when
		StreamerLoginResponseDto actualResult = streamerService.streamerLogin(loginRequestDto);

		// then
		assertThat(actualResult.getToken()).startsWith("Bearer ");
	}
}
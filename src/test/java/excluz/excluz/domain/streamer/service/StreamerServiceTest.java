package excluz.excluz.domain.streamer.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import excluz.excluz.auth.util.JwtUtil;
import excluz.excluz.common.datas.SharedData;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.domain.streamer.dto.request.StreamerLoginRequestDto;
import excluz.excluz.domain.streamer.dto.request.StreamerSignupRequestDto;
import excluz.excluz.domain.streamer.dto.response.StreamerLoginResponseDto;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import excluz.excluz.domain.user.enums.UserRole;

@ExtendWith(MockitoExtension.class)
@DisplayName("StreamerService")
class StreamerServiceTest {

	@InjectMocks
	StreamerService streamerService;

	@Mock
	StreamerRepository streamerRepository;
	@Mock
	PasswordEncoder passwordEncoder;
	@Mock
	JwtUtil jwtUtil;

	@Nested
	@DisplayName("streamerSignup 메서드")
	class StreamerSignup {

		@Test
		@DisplayName("success: 스트리머 회원가입 성공")
		void streamerSignup() {
			// given
			StreamerSignupRequestDto signupRequestDto = SharedData.STREAMER_SIGNUP_REQUEST_DTO;
			when(passwordEncoder.encode(signupRequestDto.getPassword())).thenReturn("encodedPassword");

			// when
			streamerService.streamerSignup(signupRequestDto);

			// then
			verify(passwordEncoder).encode(signupRequestDto.getPassword());
			// save() 인자 캡쳐
			ArgumentCaptor<Streamer> streamerCaptor = ArgumentCaptor.forClass(Streamer.class);
			verify(streamerRepository).save(streamerCaptor.capture());
			Streamer streamer = streamerCaptor.getValue();
			// save() 인자 검증
			assertThat(streamer.getEmail()).isEqualTo(signupRequestDto.getEmail());
			assertThat(streamer.getName()).isEqualTo(signupRequestDto.getName());
			assertThat(streamer.getNickName()).isEqualTo(signupRequestDto.getNickName());
			assertThat(streamer.getPhoneNumber()).isEqualTo(signupRequestDto.getPhoneNumber());
			assertThat(streamer.getPassword()).isEqualTo("encodedPassword");
		}
	}

	@Nested
	@DisplayName("streamerLogin 메서드")
	class StreamerLogin {
		@Test
		@DisplayName("success: 스트리머 토큰 생성 성공")
		void streamerLogin() {
			// given
			StreamerLoginRequestDto loginRequestDto = SharedData.STREAMER_LOGIN_REQUEST_DTO;
			ReflectionTestUtils.setField(SharedData.STREAMER1, "id", SharedData.STREAMER_ID1);
			String token = "token";
			StreamerLoginResponseDto loginResponseDto = new StreamerLoginResponseDto(token);

			when(streamerRepository.findByEmail(anyString())).thenReturn(Optional.of(SharedData.STREAMER1));
			when(passwordEncoder.matches(loginRequestDto.getPassword(), SharedData.STREAMER1.getPassword())).thenReturn(
				true);
			when(jwtUtil.createToken(SharedData.STREAMER1.getEmail(), SharedData.STREAMER1.getId(),
				SharedData.STREAMER1.getUserRole())).thenReturn(token);

			try (MockedStatic<StreamerLoginResponseDto> mockedStatic = mockStatic(StreamerLoginResponseDto.class)) {
				given(StreamerLoginResponseDto.from(anyString())).willReturn(loginResponseDto);

				// when
				StreamerLoginResponseDto actualResult = streamerService.streamerLogin(loginRequestDto);

				// then
				verify(streamerRepository).findByEmail(loginRequestDto.getEmail());
				verify(passwordEncoder).matches(loginRequestDto.getPassword(), SharedData.STREAMER1.getPassword());
				verify(jwtUtil).createToken(SharedData.STREAMER1.getEmail(), SharedData.STREAMER1.getId(),
					SharedData.STREAMER1.getUserRole());
				// dto 검증
				assertNotNull(actualResult);
				// token 값 검증
				assertNotNull(actualResult.getToken());
				assertFalse(actualResult.getToken().isEmpty());
				assertThat(actualResult.getToken()).startsWith(token);
			}
		}
	}
}
package excluz.excluz.domain.streamer.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import excluz.excluz.auth.util.JwtUtil;
import excluz.excluz.common.datas.SharedData;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.streamer.dto.request.StreamerLoginRequestDto;
import excluz.excluz.domain.streamer.dto.request.StreamerSignupRequestDto;
import excluz.excluz.domain.streamer.dto.request.StreamerUpdateRequestDto;
import excluz.excluz.domain.streamer.dto.response.StreamerLoginResponseDto;
import excluz.excluz.domain.streamer.dto.response.StreamerResponseDto;
import excluz.excluz.domain.streamer.dto.response.StreamerSummaryResponseDto;
import excluz.excluz.domain.streamer.repository.StreamerRepository;

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

		@Test
		@DisplayName("fail: 재입력 비밀번호 불일치 시 회원가입할 수 없음")
		void streamerSignupPasswordMismatch() {
			// given
			StreamerSignupRequestDto signupRequestDto = new StreamerSignupRequestDto(
				SharedData.STREAMER_NAME1,
				SharedData.STREAMER_NICKNAME1,
				SharedData.STREAMER_PHONE_NUMBER1,
				SharedData.STREAMER_EMAIL1,
				SharedData.STREAMER_PASSWORD1,
				SharedData.STREAMER_PASSWORD2);

			// when & then
			BadRequestException exception = assertThrows(BadRequestException.class,
				() -> streamerService.streamerSignup(signupRequestDto)
			);
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_MISMATCH);

			// 예외 발생시 save 메서드가 호출되지 않음을 검증
			verify(streamerRepository, never()).save(any(Streamer.class));
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
			when(passwordEncoder.matches(loginRequestDto.getPassword(), SharedData.STREAMER1.getPassword())).thenReturn(true);
			when(jwtUtil.createToken(SharedData.STREAMER1.getEmail(), SharedData.STREAMER1.getId(),	SharedData.STREAMER1.getUserRole())).thenReturn(token);

			try (MockedStatic<StreamerLoginResponseDto> mockedStatic = mockStatic(StreamerLoginResponseDto.class)) {
				given(StreamerLoginResponseDto.from(anyString())).willReturn(loginResponseDto);

				// when
				StreamerLoginResponseDto actualResult = streamerService.streamerLogin(loginRequestDto);

				// then
				verify(streamerRepository).findByEmail(loginRequestDto.getEmail());
				verify(passwordEncoder).matches(loginRequestDto.getPassword(), SharedData.STREAMER1.getPassword());
				verify(jwtUtil).createToken(SharedData.STREAMER1.getEmail(), SharedData.STREAMER1.getId(), SharedData.STREAMER1.getUserRole());
				// dto 검증
				assertNotNull(actualResult);
				// token 값 검증
				assertNotNull(actualResult.getToken());
				assertFalse(actualResult.getToken().isEmpty());
				assertThat(actualResult.getToken()).startsWith(token);
			}
		}
	}

	@Nested
	@DisplayName("deleteStreamer 메서드")
	class DeleteStreamer {

		@Test
		@DisplayName("success: 스트리머 본인의 계정 탈퇴")
		void deleteStreamer() {
			// given
			Streamer spyStreamer = spy(SharedData.STREAMER1);
			when(streamerRepository.findById(SharedData.STREAMER_ID1)).thenReturn(Optional.of(spyStreamer));
			when(passwordEncoder.matches(SharedData.STREAMER_PASSWORD1, spyStreamer.getPassword())).thenReturn(true);

			// 탈퇴 전 스트리머 계정 상태 검증
			assertThat(spyStreamer.isDeleted()).isEqualTo(false);

			// when
			streamerService.deleteStreamer(SharedData.STREAMER_ID1, SharedData.STREAMER_PASSWORD1);

			// then
			// 탈퇴 후 스트리머 계정 상태 검증
			assertThat(spyStreamer.isDeleted()).isEqualTo(true);

			verify(spyStreamer).updateStreamerStatus(true);
			verify(streamerRepository).findById(SharedData.STREAMER_ID1);
			verify(passwordEncoder).matches(SharedData.STREAMER_PASSWORD1, spyStreamer.getPassword());
		}
		// 실패: 비밀번호 불일치(=본인의 계정이 아님)인 경우 계정 탈퇴 불가능
	}

	@Nested
	@DisplayName("updateStreamer 메서드")
	class UpdateStreamer {

		@Test
		@DisplayName("success: 스트리머 본인 정보 수정")
		void updateStreamer() {
			// given
			StreamerUpdateRequestDto requestDto = new StreamerUpdateRequestDto(
				SharedData.STREAMER_NAME2,
				SharedData.STREAMER_NICKNAME2,
				SharedData.STREAMER_PHONE_NUMBER2,
				SharedData.STREAMER_EMAIL2,
				SharedData.STREAMER_PASSWORD1);
			StreamerResponseDto responseDto = new StreamerResponseDto(
				SharedData.STREAMER_NAME2,
				SharedData.STREAMER_NICKNAME2,
				SharedData.STREAMER_PHONE_NUMBER2,
				SharedData.STREAMER_EMAIL2);
			Streamer spyStreamer = spy(SharedData.STREAMER1);

			when(streamerRepository.findById(SharedData.STREAMER_ID1)).thenReturn(Optional.of(spyStreamer));

			try (MockedStatic<StreamerResponseDto> mockedStatic = mockStatic(StreamerResponseDto.class)) {
				given(StreamerResponseDto.from(spyStreamer)).willReturn(responseDto);

				// when
				StreamerResponseDto actualResult = streamerService.updateStreamer(SharedData.STREAMER_ID1, requestDto);

				// then
				verify(streamerRepository).findById(SharedData.STREAMER_ID1);
				verify(spyStreamer).isDeleted();
				verify(spyStreamer).updateStreamer(requestDto.getName(), requestDto.getNickName(), requestDto.getPhoneNumber(), requestDto.getEmail());
				// Dto 검증
				assertNotNull(actualResult);
				// 수정 된 값 검증
				assertThat(actualResult.getEmail()).isEqualTo(requestDto.getEmail());
				assertThat(actualResult.getName()).isEqualTo(requestDto.getName());
				assertThat(actualResult.getNickName()).isEqualTo(requestDto.getNickName());
				assertThat(actualResult.getPhoneNumber()).isEqualTo(requestDto.getPhoneNumber());
			}
		}

		// 실패: 탈퇴한 계정의 정보는 수정할 수 없음
	}

	@Nested
	@DisplayName("getPersonalInfo 메서드")
	class GetPersonalInfo {

		@Test
		@DisplayName("success: 탈퇴하지 않은 스트리머 본인 정보 조회")
		void getPersonalInfo() {
			// given
			StreamerResponseDto responseDto = new StreamerResponseDto(
				SharedData.STREAMER_NAME1,
				SharedData.STREAMER_NICKNAME1,
				SharedData.STREAMER_PHONE_NUMBER1,
				SharedData.STREAMER_EMAIL1);
			Streamer spyStreamer = spy(SharedData.STREAMER1);

			when(streamerRepository.findById(SharedData.STREAMER_ID1)).thenReturn(Optional.of(spyStreamer));

			try (MockedStatic<StreamerResponseDto> mockedStatic = mockStatic(StreamerResponseDto.class)) {
				given(StreamerResponseDto.from(spyStreamer)).willReturn(responseDto);

				// when
				StreamerResponseDto actualResult = streamerService.getPersonalInfo(SharedData.STREAMER_ID1);

				// then
				verify(streamerRepository).findById(SharedData.STREAMER_ID1);
				verify(spyStreamer).isDeleted();
				// Dto 검증
				assertNotNull(actualResult);
				// 조회 된 값 검증
				assertThat(actualResult.getEmail()).isEqualTo(spyStreamer.getEmail());
				assertThat(actualResult.getName()).isEqualTo(spyStreamer.getName());
				assertThat(actualResult.getNickName()).isEqualTo(spyStreamer.getNickName());
				assertThat(actualResult.getPhoneNumber()).isEqualTo(spyStreamer.getPhoneNumber());
			}
		}

		// 실패: 탈퇴한 본인 계정은 조회할 수 없음
	}

	@Nested
	@DisplayName("getStreamerList 메서드")
	class GetStreamerList {

		@Test
		@DisplayName("success: 닉네임을 통해 스트리머를 검색")
		void getStreamerList() {
			// given
			int page = 1;
			int size = 10;
			Pageable pageable = PageRequest.of(page - 1, size);
			StreamerSummaryResponseDto responseDto = new StreamerSummaryResponseDto(SharedData.STREAMER_NICKNAME1);

			List<Streamer> streamerList = Collections.singletonList(SharedData.STREAMER1);
			Page<Streamer> streamerPage = new PageImpl<>(streamerList, pageable, streamerList.size());

			when(streamerRepository.findByNickName(pageable, SharedData.STREAMER_NICKNAME1)).thenReturn(streamerPage);

			try (MockedStatic<StreamerSummaryResponseDto> mockedStatic = mockStatic(StreamerSummaryResponseDto.class)) {
				given(StreamerSummaryResponseDto.from(SharedData.STREAMER1)).willReturn(responseDto);

				// when
				Page<StreamerSummaryResponseDto> actualResult = streamerService.getStreamerList(page, size,	SharedData.STREAMER_NICKNAME1);

				// then
				verify(streamerRepository).findByNickName(pageable, SharedData.STREAMER_NICKNAME1);
				assertThat(actualResult).isNotNull();

				StreamerSummaryResponseDto dto = actualResult.getContent().get(0);
				assertThat(dto.getNickName()).isEqualTo(SharedData.STREAMER1.getNickName());
			}
		}
	}

	@Nested
	@DisplayName("getStreamer 메서드")
	class GetStreamer {

		@Test
		@DisplayName("success: 탈퇴하지 않은 스트리머 단건 조회")
		void getStreamer() {
			// given
			Streamer spyStreamer = spy(SharedData.STREAMER1);
			StreamerSummaryResponseDto responseDto = new StreamerSummaryResponseDto(SharedData.STREAMER_NICKNAME1);
			when(streamerRepository.findById(SharedData.STREAMER_ID1)).thenReturn(Optional.of(spyStreamer));

			try (MockedStatic<StreamerSummaryResponseDto> mockedStatic = mockStatic(StreamerSummaryResponseDto.class)) {
				given(StreamerSummaryResponseDto.from(spyStreamer)).willReturn(responseDto);

				// when
				StreamerSummaryResponseDto actualResult = streamerService.getStreamer(SharedData.STREAMER_ID1);

				// then
				verify(streamerRepository).findById(SharedData.STREAMER_ID1);
				verify(spyStreamer).isDeleted();

				assertNotNull(actualResult);
				assertThat(actualResult.getNickName()).isEqualTo(spyStreamer.getNickName());
			}
		}

	}
}
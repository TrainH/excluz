package excluz.excluz.domain.store.store.service;

import static org.assertj.core.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

import excluz.excluz.common.datas.SharedData;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.dto.request.StoreDeleteRequestDto;
import excluz.excluz.domain.store.store.dto.request.StoreRequestDto;
import excluz.excluz.domain.store.store.dto.request.StoreUpdateRequestDto;
import excluz.excluz.domain.store.store.dto.response.StoreResponseDto;
import excluz.excluz.domain.store.store.dto.response.StoreUpdateResponseDto;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import excluz.excluz.domain.streamer.service.StreamerService;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreServiceTest")
public class StoreServiceTest {

	@InjectMocks
	StoreService storeService;

	@Mock
	StoreRepository storeRepository;
	@Mock
	ItemRepository itemRepository;
	@Mock
	StreamerRepository streamerRepository;
	@Mock
	StreamerService streamerService;
	@Mock
	PasswordEncoder passwordEncoder;
	@Mock
	Store mockStore;
	@Mock
	Streamer mockStreamer;

	@Nested
	@DisplayName("createStore 메서드")
	class CreateStore {
		@Test
		@DisplayName("success: 스토어 생성 성공")
		void create_store_success() {

			// given
			StoreRequestDto requestDto = new StoreRequestDto(SharedData.ADDRESS1, SharedData.STORE_NAME1, SharedData.REGISTRATION_NUMBER1);
			Store store = SharedData.STORE1;

			when(streamerService.findStreamerById(anyInt())).thenReturn(SharedData.STREAMER1);
			when(storeRepository.save(any(Store.class))).thenReturn(store);

			// when
			storeService.createStore(requestDto, SharedData.STREAMER_ID1);

			// then
			verify(streamerService).findStreamerById(SharedData.STREAMER_ID1);
			// 저장 시 전달된 Store 객체의 필드를 검증
			ArgumentCaptor<Store> storeCaptor = ArgumentCaptor.forClass(Store.class);
			verify(storeRepository).save(storeCaptor.capture());
			Store savedStore = storeCaptor.getValue();

			assertThat(savedStore.getAddress()).isEqualTo(requestDto.getAddress());
			assertThat(savedStore.getStoreName()).isEqualTo(requestDto.getStoreName());
			assertThat(savedStore.getRegistrationNumber()).isEqualTo(requestDto.getRegistrationNumber());
			assertThat(savedStore.getStreamer()).isEqualTo(SharedData.STREAMER1);
		}
	}

	@Nested
	@DisplayName("deleteStore 메서드")
	class DeleteStore {

		@Test
		@DisplayName("success: 스토어 소프트 딜리트 정상 호출")
		void delete_store_success() {
			// given
			StoreDeleteRequestDto deleteRequestDto = new StoreDeleteRequestDto(SharedData.STREAMER_PASSWORD1);
			Store spyStore = spy(SharedData.STORE1);

			when(streamerService.findStreamerById(anyInt())).thenReturn(SharedData.STREAMER1);
			when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
			when(storeRepository.findById(SharedData.STORE_ID1)).thenReturn(Optional.of(spyStore));

			// 삭제 상태 변경 전 검증
			assertThat(spyStore.isDeleted()).isEqualTo(false);

			// when
			storeService.deleteStore(deleteRequestDto, SharedData.STREAMER_ID1, SharedData.STORE_ID1);

			// then
			verify(streamerService).findStreamerById(SharedData.STREAMER_ID1);
			verify(passwordEncoder).matches(SharedData.STREAMER_PASSWORD1, SharedData.STREAMER_PASSWORD1);
			verify(storeRepository).findById(SharedData.STORE_ID1);
			verify(spyStore).updateIsDeleted(true);

			// 삭제 상태 변경 후 검증
			assertThat(spyStore.isDeleted()).isEqualTo(true);
		}

		// 실패: 비밀번호 불일치
		// 실패: 이미 삭제된 스토어
	}

	@Nested
	@DisplayName("updateStore 메서드")
	class UpdateStore {

		@Test
		@DisplayName("success: 스토어 주인은 스토어 정보 수정 가능")
		void update_store_success() {
			// given
			StoreUpdateRequestDto requestDto = new StoreUpdateRequestDto(SharedData.ADDRESS2, SharedData.STORE_NAME2, SharedData.REGISTRATION_NUMBER2);
			StoreUpdateResponseDto responseDto = new StoreUpdateResponseDto(SharedData.ADDRESS2, SharedData.STORE_NAME2, SharedData.REGISTRATION_NUMBER2);
			Store spyStore = spy(SharedData.STORE1);
			when(storeRepository.findById(anyInt())).thenReturn(Optional.of(spyStore));
			// 스토어 주인 검증 로직 stub
			when(spyStore.getStreamer()).thenReturn(mockStreamer);
			when(mockStreamer.getId()).thenReturn(SharedData.STREAMER_ID1);

			// 정보 수정 전 검증
			assertThat(spyStore.getAddress()).isEqualTo(SharedData.ADDRESS1);
			assertThat(spyStore.getStoreName()).isEqualTo(SharedData.STORE_NAME1);
			assertThat(spyStore.getRegistrationNumber()).isEqualTo(SharedData.REGISTRATION_NUMBER1);

			try (MockedStatic<StoreUpdateResponseDto> mockedStatic = mockStatic(StoreUpdateResponseDto.class)) {
				given(StoreUpdateResponseDto.from(spyStore)).willReturn(responseDto);

				// when
				StoreUpdateResponseDto actualResult = storeService.updateStore(SharedData.STREAMER_ID1, SharedData.STORE_ID1, requestDto);

				// then
				verify(storeRepository).findById(SharedData.STORE_ID1);
				verify(spyStore).updateStore(SharedData.ADDRESS2, SharedData.STORE_NAME2, SharedData.REGISTRATION_NUMBER2);

				// 정보 수정 후 검증
				assertThat(actualResult.getAddress()).isEqualTo(SharedData.ADDRESS2);
				assertThat(actualResult.getStoreName()).isEqualTo(SharedData.STORE_NAME2);
				assertThat(actualResult.getRegistrationNumber()).isEqualTo(SharedData.REGISTRATION_NUMBER2);
			}
		}

		// 실패: 스토어 주인이 아닐 경우 스토어 정보 수정 불가
		// 실패: 소프트 딜리트된 스토어는 수정 불가
	}
}

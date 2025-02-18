package excluz.excluz.domain.store.store.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import excluz.excluz.common.datas.SharedData;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.dto.request.StoreRequestDto;
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

	@Nested
	@DisplayName("createStore 메서드")
	class CreateStore {
		@Test
		@DisplayName("success: 스토어 생성 성공")
		void createStore_success() {

			// given
			StoreRequestDto requestDto = new StoreRequestDto(SharedData.ADDRESS1, SharedData.STORE_NAME1, SharedData.REGISTRATION_NUMBER1);
			Store store = SharedData.STORE1;
			Streamer mockStreamer = mock(Streamer.class);

			when(mockStreamer.isDeleted()).thenReturn(false);
			when(streamerService.findStreamerById(anyInt())).thenReturn(mockStreamer);
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
			assertThat(savedStore.getStreamer()).isEqualTo(mockStreamer);
		}
	}
}

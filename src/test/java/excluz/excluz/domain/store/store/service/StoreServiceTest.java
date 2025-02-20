package excluz.excluz.domain.store.store.service;

import static org.assertj.core.api.Assertions.*;

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

import excluz.excluz.common.datas.SharedData;
import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.dto.request.StoreDeleteRequestDto;
import excluz.excluz.domain.store.store.dto.request.StoreRequestDto;
import excluz.excluz.domain.store.store.dto.request.StoreUpdateRequestDto;
import excluz.excluz.domain.store.store.dto.response.StoreDetailResponseDto;
import excluz.excluz.domain.store.store.dto.response.StoreNameResponseDto;
import excluz.excluz.domain.store.store.dto.response.StoreResponseDto;
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
		void createStore() {

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
		void deleteStore() {
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

		// 실패: 비밀번호가 불일치할 경우 스토어 삭제(soft-delete)를 할 수 없음
		// 실패: 삭제(soft-delete)된 스토어에 대해서는 삭제 작업을 할 수 없음
	}

	@Nested
	@DisplayName("updateStore 메서드")
	class UpdateStore {

		@Test
		@DisplayName("success: 스토어 주인은 스토어 정보 수정 가능")
		void updateStore() {
			// given
			StoreUpdateRequestDto requestDto = new StoreUpdateRequestDto(SharedData.ADDRESS2, SharedData.STORE_NAME2, SharedData.REGISTRATION_NUMBER2);
			StoreResponseDto responseDto = new StoreResponseDto(SharedData.STORE_ID1, SharedData.ADDRESS2, SharedData.STORE_NAME2, SharedData.REGISTRATION_NUMBER2);
			Store spyStore = spy(SharedData.STORE1);
			when(storeRepository.findById(anyInt())).thenReturn(Optional.of(spyStore));
			// 스토어 주인 검증 로직 stub
			when(spyStore.getStreamer()).thenReturn(mockStreamer);
			when(mockStreamer.getId()).thenReturn(SharedData.STREAMER_ID1);

			// 정보 수정 전 검증
			assertThat(spyStore.getAddress()).isEqualTo(SharedData.ADDRESS1);
			assertThat(spyStore.getStoreName()).isEqualTo(SharedData.STORE_NAME1);
			assertThat(spyStore.getRegistrationNumber()).isEqualTo(SharedData.REGISTRATION_NUMBER1);

			try (MockedStatic<StoreResponseDto> mockedStatic = mockStatic(StoreResponseDto.class)) {
				given(StoreResponseDto.from(spyStore)).willReturn(responseDto);

				// when
				StoreResponseDto actualResult = storeService.updateStore(SharedData.STREAMER_ID1, SharedData.STORE_ID1, requestDto);

				// then
				verify(storeRepository).findById(SharedData.STORE_ID1);
				verify(spyStore).updateStore(SharedData.ADDRESS2, SharedData.STORE_NAME2,
					SharedData.REGISTRATION_NUMBER2);

				// 정보 수정 후 검증
				assertThat(actualResult.getAddress()).isEqualTo(SharedData.ADDRESS2);
				assertThat(actualResult.getStoreName()).isEqualTo(SharedData.STORE_NAME2);
				assertThat(actualResult.getRegistrationNumber()).isEqualTo(SharedData.REGISTRATION_NUMBER2);
			}
		}

		// 실패: 스토어 주인이 아닐 경우 스토어 정보를 수정할 수 없음
		// 실패: 삭제(soft-delete)된 스토어는 정보를 수정할 수 없음
	}

	@Nested
	@DisplayName("getStoreList 메서드")
	class GetStoreList {

		@Test
		@DisplayName("success: 스토어 목록 조회 성공")
		void getStoreList() {
			// given
			int page = 1;
			int size = 10;
			Pageable pageable = PageRequest.of(page-1, size);
			Store store = SharedData.STORE1;
			List<Store> storeList = Collections.singletonList(store);
			Page<Store> storePage = new PageImpl<>(storeList, pageable, storeList.size());

			when(storeRepository.findByStoreName(pageable, SharedData.STORE_NAME1)).thenReturn(storePage);

			// when
			Page<StoreNameResponseDto> actualResult = storeService.getStoreList(SharedData.STORE_NAME1, page, size);

			// then
			verify(storeRepository).findByStoreName(pageable, SharedData.STORE_NAME1);

			assertThat(actualResult).isNotNull();
			StoreNameResponseDto storeResponseDto = actualResult.getContent().get(0);
			assertThat(storeResponseDto.getStoreName()).isEqualTo(store.getStoreName());
		}

		// 실패: 페이지 사이즈가 0 이하일 경우 예외 발생
	}

	@Nested
	@DisplayName("getStoreById 메서드")
	class GetStoreById {

		@Test
		@DisplayName("success: 스토어 아이디를 통해 스토어 단건 조회 성공")
		void getStoreById() {
			// given
			int page = 1;
			int size = 10;
			Pageable pageable = PageRequest.of(page-1, size);
			when(storeRepository.findStreamerWithStore(anyInt())).thenReturn(Optional.of(SharedData.STREAMER1));

			when(storeRepository.findById(SharedData.STORE_ID1)).thenReturn(Optional.of(SharedData.STORE1));

			List<Item> itemList = Collections.singletonList(SharedData.ITEM1);
			Page<Item> itemPage = new PageImpl<>(itemList, pageable, itemList.size());
			when(itemRepository.findByStoreId(SharedData.STORE_ID1, pageable)).thenReturn(itemPage);

			// when
			StoreDetailResponseDto actualResult = storeService.getStoreById(SharedData.STORE_ID1, page, size);

			// then
			verify(storeRepository).findStreamerWithStore(SharedData.STORE_ID1);
			verify(storeRepository).findById(SharedData.STORE_ID1);
			verify(itemRepository).findByStoreId(SharedData.STORE_ID1, pageable);

			// 데이터가 Dto로 바르게 변환되는지 검증
			assertThat(actualResult.getNickName()).isEqualTo(SharedData.STREAMER1.getNickName());
			assertThat(actualResult.getAddress()).isEqualTo(SharedData.STORE1.getAddress());
			assertThat(actualResult.getStoreName()).isEqualTo(SharedData.STORE1.getStoreName());
			assertThat(actualResult.getRegistrationNumber()).isEqualTo(SharedData.STORE1.getRegistrationNumber());
			assertThat(actualResult.getItemList()).hasSize(itemList.size());
		}
		// 실패: 스토어 아이디로 조회되는 유저가 없을 경우 예외 발생
	}

	@Nested
	@DisplayName("getOwnedStoreById 메서드")
	class GetOwnedStoreById {

		@Test
		@DisplayName("success: 사용자 아이디로 본인의 가게 조회")
		void getOwnedStoreById() {
			// given
			// store 객체의 id를 SharedData.STORE_ID1로 설정
			ReflectionTestUtils.setField(SharedData.STORE1, "id", SharedData.STORE_ID1);
			int page = 1;
			int size = 10;
			Pageable pageable = PageRequest.of(page-1, size);
			when(streamerService.findStreamerById(SharedData.STREAMER_ID1)).thenReturn(SharedData.STREAMER1);
			when(storeRepository.findStoreWithStreamer(SharedData.STREAMER_ID1)).thenReturn(Optional.of(SharedData.STORE1));

			List<Item> itemList = Collections.singletonList(SharedData.ITEM1);
			Page<Item> itemPage = new PageImpl<>(itemList, pageable, itemList.size());
			when(itemRepository.findByStoreId(SharedData.STORE1.getId(), pageable)).thenReturn(itemPage);

			// when
			StoreDetailResponseDto actualResult = storeService.getOwnedStoreById(SharedData.STREAMER_ID1, page, size);

			// then
			verify(streamerService).findStreamerById(SharedData.STREAMER_ID1);
			verify(storeRepository).findStoreWithStreamer(SharedData.STREAMER_ID1);
			verify(itemRepository).findByStoreId(SharedData.STORE_ID1, pageable);

			// 데이터가 Dto로 바르게 변환되는지 검증
			assertThat(actualResult.getNickName()).isEqualTo(SharedData.STREAMER1.getNickName());
			assertThat(actualResult.getAddress()).isEqualTo(SharedData.STORE1.getAddress());
			assertThat(actualResult.getStoreName()).isEqualTo(SharedData.STORE1.getStoreName());
			assertThat(actualResult.getRegistrationNumber()).isEqualTo(SharedData.STORE1.getRegistrationNumber());
			assertThat(actualResult.getItemList()).hasSize(itemList.size());
		}

		// 실패: 탈퇴한 스트리머는 본인의 가게를 조회할 수 없음
		// 실패: 가게가 삭제(soft-delete)된 경우 조회할 수 없음
	}
}

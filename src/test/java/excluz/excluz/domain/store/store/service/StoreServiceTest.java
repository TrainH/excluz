package excluz.excluz.domain.store.store.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.service.StreamerService;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

	@InjectMocks
	StoreService storeService;

	@Mock
	StoreRepository storeRepository;
	@Mock
	ItemRepository itemRepository;
	@Mock
	StreamerService streamerService;

	/* 공유 데이터 */
	// Streamer 데이터
	public final static Integer TEST_STREAMER_ID1 = 1;
	public final static String TEST_STREAMER_NAME1 = "홍길동";
	public final static String TEST_STREAMER_NICKNAME1 = "암행어사";
	public final static String TEST_STREAMER_PHONE_NUMBER1 = "010-1234-1234";
	public final static String TEST_STREAMER_EMAIL1 = "test12@test.com";
	public final static String TEST_PASSWORD1 = "Qwer1234!!!!";
	public final static Streamer TEST_STREAMER1 = new Streamer(TEST_STREAMER_NAME1, TEST_STREAMER_NICKNAME1, TEST_STREAMER_PHONE_NUMBER1, TEST_STREAMER_EMAIL1, TEST_PASSWORD1);

	// Store 데이터
	public final static Integer TEST_STORE_ID1 = 1;
	public final static String TEST_ADDRESS1 = "사랑시 평화동 정의로12";
	public final static String TEST_STORE_NAME1 = "테스트샵";
	public final static String TEST_REGISTRATION_NUMBER1 = "123-12-12345";
	public final static Store TEST_STORE1 = new Store(TEST_STREAMER1, TEST_ADDRESS1, TEST_STORE_NAME1,TEST_REGISTRATION_NUMBER1);

	@Test
	@DisplayName("success: 스토어 생성 성공")
	void createStore_success(){

		// given
		Streamer streamer = TEST_STREAMER1;
		Store store = TEST_STORE1;

		when(storeRepository.save(any(Store.class))).thenReturn(store);

		// when
		Store actualResult = storeRepository.save(store);

		// then
		// 상태검증
		assertThat(actualResult.getAddress()).isEqualTo(store.getAddress());
		assertThat(actualResult.getStoreName()).isEqualTo(store.getStoreName());
		assertThat(actualResult.getRegistrationNumber()).isEqualTo(store.getRegistrationNumber());

		// 행위 검증: save()메서드 호출 성공
		verify(storeRepository, times(1)).save(any(Store.class));
	}


}

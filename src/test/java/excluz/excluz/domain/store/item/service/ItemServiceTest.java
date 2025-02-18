package excluz.excluz.domain.store.item.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.domain.store.item.dto.request.ItemCreateRequestDto;
import excluz.excluz.domain.store.item.dto.request.ItemUpdateRequestDto;
import excluz.excluz.domain.store.item.dto.response.ItemResponseDto;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.dto.request.StreamerLoginRequestDto;
import excluz.excluz.domain.streamer.dto.request.StreamerSignupRequestDto;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

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

	// Store 데이터
	public final static Integer TEST_STORE_ID1 = 1;
	public final static String TEST_ADDRESS1 = "사랑시 평화동 정의로12";
	public final static String TEST_STORE_NAME1 = "테스트샵";
	public final static String TEST_REGISTRATION_NUMBER1 = "123-12-12345";
	public final static Store TEST_STORE1 = new Store(TEST_STREAMER1, TEST_ADDRESS1, TEST_STORE_NAME1,TEST_REGISTRATION_NUMBER1);

	// Item 데이터
	public final static Integer TEST_ITEM_ID1 = 1;
	public final static String TEST_ITEM_NAME1 = "테스트 굿즈";
	public final static String TEST_ITEM_NAME2 = "업데이트 굿즈";
	public final static String TEST_EXPLANATION1 = "굿즈 설명";
	public final static String TEST_EXPLANATION2 = "업데이트 굿즈 설명";
	public final static Integer TEST_PRICE1 = 3000;
	public final static Integer TEST_PRICE2 = 4000;
	public final static Integer TEST_REMAINING_QUANTITY1 = 100;
	public final static Integer TEST_REMAINING_QUANTITY2 = 200;
	public final static Item TEST_UPDATED_ITEM = new Item(TEST_STORE1, TEST_ITEM_NAME2, TEST_EXPLANATION2, TEST_PRICE2, TEST_REMAINING_QUANTITY2);
	public final static Item TEST_ITEM1 = new Item(TEST_STORE1, TEST_ITEM_NAME1, TEST_EXPLANATION1, TEST_PRICE1, TEST_REMAINING_QUANTITY1);
	public final static Item TEST_ITEM2 = new Item(TEST_STORE1, TEST_ITEM_NAME2, TEST_EXPLANATION2, TEST_PRICE2, TEST_REMAINING_QUANTITY2);
	public final static ItemCreateRequestDto TEST_ITEM_CREATE_REQUEST_DTO = new ItemCreateRequestDto(TEST_ITEM_NAME1, TEST_EXPLANATION1, TEST_PRICE1, TEST_REMAINING_QUANTITY1);
	public final static ItemUpdateRequestDto TEST_ITEM_UPDATE_REQUEST_DTO = new ItemUpdateRequestDto(TEST_ITEM_NAME2, TEST_EXPLANATION2, TEST_PRICE2, TEST_REMAINING_QUANTITY2);
	public final static ItemResponseDto TEST_ITEM_RESPONSE_DTO = new ItemResponseDto(TEST_ITEM_NAME2, TEST_EXPLANATION2, TEST_PRICE2, TEST_REMAINING_QUANTITY2);

	@InjectMocks
	ItemService itemService;

	@Mock
	ItemRepository itemRepository;
	@Mock
	StoreRepository storeRepository;
	@Mock
	Item mockItem;
	@Mock
	Store mockStore;
	@Mock
	Streamer mockStreamer;

	@Nested
	@DisplayName("기본 설정이 필요한 테스트")
	class WithStubbing {

		@BeforeEach
		void setUp() {
			when(mockItem.getStore()).thenReturn(mockStore);
			when(mockStore.getStreamer()).thenReturn(mockStreamer);
			when(mockStreamer.getId()).thenReturn(1);
		}

		@Test
		@DisplayName("success: 아이템 소프트 딜리트 정상 수행")
		void deleteItem_SoftDelete_success() {
			// given
			when(itemRepository.findItemByIdAndNotDeleted(anyInt())).thenReturn(Optional.of(mockItem));
			when(mockItem.getStore().getStreamer().getId()).thenReturn(1);

			// when
			itemService.deleteItem(TEST_ITEM_ID1, TEST_STREAMER_ID1);

			// then
			verify(mockItem, times(1)).updateIsDeleted(true);
		}

		@Test
		@DisplayName("success: 아이템 정보 수정 성공")
		void update_ItemInfo_success() {
			// given
			Item updatedItem = TEST_UPDATED_ITEM;

			when(itemRepository.findItemByIdAndNotDeleted(anyInt())).thenReturn(Optional.of(mockItem));
			when(mockItem.getStore().getStreamer().getId()).thenReturn(1);

			try (MockedStatic<ItemResponseDto> itemMockedStatic = mockStatic(ItemResponseDto.class)) {
				given(ItemResponseDto.from(any(Item.class))).willReturn(TEST_ITEM_RESPONSE_DTO);

				// when
				ItemResponseDto actualResult = itemService.updateItemInfo(TEST_ITEM_UPDATE_REQUEST_DTO, TEST_ITEM_ID1,
					TEST_STREAMER_ID1);

				// then
				verify(mockItem, times(1)).updateItem(anyString(), anyString(), anyInt(), anyInt());

				assertThat(actualResult.getItemName()).isEqualTo(updatedItem.getItemName());
				assertThat(actualResult.getExplanation()).isEqualTo(updatedItem.getExplanation());
				assertThat(actualResult.getPrice()).isEqualTo(updatedItem.getPrice());
				assertThat(actualResult.getRemainingQuantity()).isEqualTo(updatedItem.getRemainingQuantity());
			}
		}
	}

	@Nested
	@DisplayName("기본 설정이 불필요한 테스트")
	class WithoutStubbing {

		@Test
		@DisplayName("success: 아이템 생성 기능 정상 수행")
		void createItem_success() {
			// given
			when(storeRepository.findStoreWithStreamer(TEST_STREAMER_ID1)).thenReturn(Optional.of(TEST_STORE1));
			when(itemRepository.save(any(Item.class))).thenReturn(TEST_ITEM1);

			// when
			itemService.createItem(TEST_ITEM_CREATE_REQUEST_DTO, TEST_STREAMER_ID1);

			// then
			verify(itemRepository, times(1)).save(any(Item.class));
		}

		@Test
		@DisplayName("success: itemId로 아이템 단건 조회")
		void getItemById_success() {
			// given
			Item item = TEST_ITEM2;

			when(itemRepository.findItemByIdAndNotDeleted(eq(item.getId()))).thenReturn(Optional.of(item));

			try (MockedStatic<ItemResponseDto> itemMockedStatic = mockStatic(ItemResponseDto.class)) {
				given(ItemResponseDto.from(any(Item.class))).willReturn(TEST_ITEM_RESPONSE_DTO);

				// when
				ItemResponseDto actualResult = itemService.getItemById(item.getId());

				// then
				assertThat(actualResult.getItemName()).isEqualTo(item.getItemName());
				assertThat(actualResult.getExplanation()).isEqualTo(item.getExplanation());
				assertThat(actualResult.getPrice()).isEqualTo(item.getPrice());
				assertThat(actualResult.getRemainingQuantity()).isEqualTo(item.getRemainingQuantity());
			}
		}
	}
}
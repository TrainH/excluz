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

import excluz.excluz.common.datas.SharedData;
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
@DisplayName("ItemService의")
class ItemServiceTest {

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
	@DisplayName("UpdateItem 메서드는")
	class UpdateItem {

		@BeforeEach
		void setUp() {
			when(mockItem.getStore()).thenReturn(mockStore);
			when(mockStore.getStreamer()).thenReturn(mockStreamer);
			when(mockStreamer.getId()).thenReturn(1);
		}

		@Test
		@DisplayName("success: 아이템 정보 수정 성공")
		void update_ItemInfo_success() {
			// given
			Item updatedItem = SharedData.UPDATED_ITEM;

			when(itemRepository.findItemByIdAndNotDeleted(anyInt())).thenReturn(Optional.of(mockItem));
			when(mockItem.getStore().getStreamer().getId()).thenReturn(1);

			try (MockedStatic<ItemResponseDto> itemMockedStatic = mockStatic(ItemResponseDto.class)) {
				given(ItemResponseDto.from(any(Item.class))).willReturn(SharedData.ITEM_RESPONSE_DTO);

				// when
				ItemResponseDto actualResult = itemService.updateItemInfo(SharedData.ITEM_UPDATE_REQUEST_DTO, SharedData.ITEM_ID1,
					SharedData.STREAMER_ID1);

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
	@DisplayName("deleteItem 메서드는")
	class DeleteItem {

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
			itemService.deleteItem(SharedData.ITEM_ID1, SharedData.STREAMER_ID1);

			// then
			verify(mockItem, times(1)).updateIsDeleted(true);
		}

	}

	@Nested
	@DisplayName("getItemById 메서드는")
	class GetItemById {

		@Test
		@DisplayName("success: itemId로 아이템 단건 조회")
		void getItemById_success() {
			// given
			Item item = SharedData.ITEM2;

			when(itemRepository.findItemByIdAndNotDeleted(eq(item.getId()))).thenReturn(Optional.of(item));

			try (MockedStatic<ItemResponseDto> itemMockedStatic = mockStatic(ItemResponseDto.class)) {
				given(ItemResponseDto.from(any(Item.class))).willReturn(SharedData.ITEM_RESPONSE_DTO);

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

	@Nested
	@DisplayName("createItem 메서드는")
	class CreateItem {

		@Test
		@DisplayName("success: 아이템 생성 기능 정상 수행")
		void createItem_success() {
			// given
			when(storeRepository.findStoreWithStreamer(1)).thenReturn(Optional.of(SharedData.STORE1));
			when(itemRepository.save(any(Item.class))).thenReturn(SharedData.ITEM1);

			// when
			itemService.createItem(SharedData.ITEM_CREATE_REQUEST_DTO, 1);

			// then
			verify(itemRepository, times(1)).save(any(Item.class));
		}
	}
}
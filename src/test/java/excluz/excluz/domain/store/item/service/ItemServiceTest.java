package excluz.excluz.domain.store.item.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import excluz.excluz.common.datas.SharedData;
import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.store.item.dto.response.ItemResponseDto;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService")
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
	@DisplayName("UpdateItem 메서드")
	class UpdateItem {

		@BeforeEach
		void setUp() {
			when(mockItem.getStore()).thenReturn(mockStore);
			when(mockStore.getStreamer()).thenReturn(mockStreamer);
			when(mockStreamer.getId()).thenReturn(1);
		}

		@Test
		@DisplayName("success: 아이템 정보 수정 성공")
		void updateItemInfo() {
			// given
			Item updatedItem = SharedData.UPDATED_ITEM;

			when(itemRepository.findItemByIdAndNotDeleted(anyInt())).thenReturn(Optional.of(mockItem));
			when(mockItem.getStore().getStreamer().getId()).thenReturn(1);

			try (MockedStatic<ItemResponseDto> itemMockedStatic = mockStatic(ItemResponseDto.class)) {
				given(ItemResponseDto.from(any(Item.class))).willReturn(SharedData.ITEM_RESPONSE_DTO);

				// when
				ItemResponseDto actualResult = itemService.updateItemInfo(SharedData.ITEM_UPDATE_REQUEST_DTO,
					SharedData.ITEM_ID1,
					SharedData.STREAMER_ID1);

				// then
				verify(itemRepository, times(1)).findItemByIdAndNotDeleted(anyInt());

				verify(mockItem, atLeastOnce()).getStore();
				verify(mockStore, atLeastOnce()).getStreamer();
				verify(mockStreamer, atLeastOnce()).getId();

				verify(mockItem, times(1)).updateItem(anyString(), anyString(), anyInt(), anyInt());

				assertThat(actualResult.getItemName()).isEqualTo(updatedItem.getItemName());
				assertThat(actualResult.getExplanation()).isEqualTo(updatedItem.getExplanation());
				assertThat(actualResult.getPrice()).isEqualTo(updatedItem.getPrice());
				assertThat(actualResult.getRemainingQuantity()).isEqualTo(updatedItem.getRemainingQuantity());
			}
		}
	}

	@Nested
	@DisplayName("deleteItem 메서드")
	class DeleteItem {

		@BeforeEach
		void setUp() {
			when(mockItem.getStore()).thenReturn(mockStore);
			when(mockStore.getStreamer()).thenReturn(mockStreamer);
			when(mockStreamer.getId()).thenReturn(1);
		}

		@Test
		@DisplayName("success: 아이템 소프트 딜리트 정상 수행")
		void softDeleteItem() {
			// given
			when(itemRepository.findItemByIdAndNotDeleted(anyInt())).thenReturn(Optional.of(mockItem));
			when(mockItem.getStore().getStreamer().getId()).thenReturn(1);

			// when
			itemService.deleteItem(SharedData.ITEM_ID1, SharedData.STREAMER_ID1);

			// then
			verify(itemRepository).findItemByIdAndNotDeleted(anyInt());

			verify(mockItem, atLeastOnce()).getStore();
			verify(mockStore, atLeastOnce()).getStreamer();
			verify(mockStreamer, atLeastOnce()).getId();

			verify(mockItem).updateIsDeleted(true);
		}
		@Test
		@DisplayName("fail: 아이템 삭제 권한 없음 (ForbiddenException 발생)")
		void deleteItemForbidden() {
			// given
			when(itemRepository.findItemByIdAndNotDeleted(anyInt())).thenReturn(Optional.of(mockItem));
			when(mockItem.getStore()).thenReturn(mockStore);
			when(mockStore.getStreamer()).thenReturn(mockStreamer);
			when(mockStreamer.getId()).thenReturn(SharedData.STREAMER_ID1);

			// when, then
			assertThatThrownBy(() -> itemService.deleteItem(SharedData.ITEM_ID1, SharedData.STREAMER_ID2))
				.isInstanceOf(ForbiddenException.class); // Only verifying exception type, no message validation
		}
	}
	/**
	 * 위 DeleteItem에 속하는 메서드지만, 위에 넣으면 setUp()과 충돌하는 이슈 발생. (아이템이 없어야 하는데, 아이템이 존재하도록 세팅돼서)
	 * 이슈 해결을 위해선 setUp()과 softDeleteItem()를 모두 건드려야 해서
	 * deleteItemNotFound만 따로 빼서 DeleteItem 실패 테스트코드 진행했습니다.
	 */
	@Test
	@DisplayName("fail: 존재하지 않는 아이템 삭제 (예외 발생)")
	void deleteItemNotFound() {
		// given
		when(itemRepository.findItemByIdAndNotDeleted(anyInt())).thenReturn(Optional.empty()); // 아이템 없음

		// when, then
		assertThatThrownBy(() -> itemService.deleteItem(SharedData.ITEM_ID1, SharedData.STREAMER_ID1))
			.isInstanceOf(NotFoundException.class); // NotFoundException 발생 확인

		verify(itemRepository, times(1)).findItemByIdAndNotDeleted(anyInt()); // findItemByIdAndNotDeleted가 1번 실행되었는지 확인
	}

	@Nested
	@DisplayName("getItemById 메서드")
	class GetItemById {

		@Test
		@DisplayName("success: itemId로 아이템 단건 조회")
		void getItemByIdSuccess() {
			// given
			Item item = SharedData.ITEM2;
			when(itemRepository.findItemByIdAndNotDeleted(eq(item.getId()))).thenReturn(Optional.of(item));

			try (MockedStatic<ItemResponseDto> itemMockedStatic = mockStatic(ItemResponseDto.class)) {
				given(ItemResponseDto.from(any(Item.class))).willReturn(SharedData.ITEM_RESPONSE_DTO);

				// when
				ItemResponseDto actualResult = itemService.getItemById(item.getId());

				// then
				verify(itemRepository).findItemByIdAndNotDeleted(eq(item.getId()));

				assertThat(actualResult.getItemName()).isEqualTo(item.getItemName());
				assertThat(actualResult.getExplanation()).isEqualTo(item.getExplanation());
				assertThat(actualResult.getPrice()).isEqualTo(item.getPrice());
				assertThat(actualResult.getRemainingQuantity()).isEqualTo(item.getRemainingQuantity());
			}
		}
	}

	@Nested
	@DisplayName("createItem 메서드")
	class CreateItem {

		@Test
		@DisplayName("success: 아이템 생성 기능 정상 수행")
		void createItemSuccess() {
			// given
			when(storeRepository.findStoreWithStreamer(anyInt())).thenReturn(Optional.of(SharedData.STORE1));
			when(itemRepository.save(any(Item.class))).thenReturn(SharedData.ITEM1);

			// when
			itemService.createItem(SharedData.ITEM_CREATE_REQUEST_DTO, 1);

			// then
			verify(storeRepository).findStoreWithStreamer(anyInt());
			verify(itemRepository).save(any(Item.class));
		}

		@Test
		@DisplayName("fail: 존재하지 않는 스토어 (예외 발생)")
		void createItemStoreNotFound() {
			// given
			Integer streamerId = 999; // 존재하지 않는 스트리머 ID

			when(storeRepository.findStoreWithStreamer(streamerId)).thenReturn(Optional.empty()); // 스토어 찾을 수 없음

			// when, then
			assertThatThrownBy(() -> itemService.createItem(SharedData.ITEM_CREATE_REQUEST_DTO, streamerId))
				.isInstanceOf(NotFoundException.class); // NotFoundException 예외 발생 확인

			verify(storeRepository, times(1)).findStoreWithStreamer(streamerId); // findStoreWithStreamer()가 1번 호출되었는지 확인
		}
	}

	@Nested
	@DisplayName("getItemList 메서드")
	class GetItemList {

		@Test
		@DisplayName("success: 일반 케이스 - minPrice와 maxPrice가 정상 범위임")
		void getItemListStandard() {
			// given
			Pageable pageable = PageRequest.of(1, 10);
			when(itemRepository.findHighestItemPrice()).thenReturn(Optional.of(5000));
			List<Item> itemList = Collections.singletonList(SharedData.ITEM2);
			Page<Item> itemPage = new PageImpl<>(itemList, pageable, itemList.size());
			when(itemRepository.findByPriceWithItemName(eq(pageable), anyInt(), anyInt(), anyString())).thenReturn(
				itemPage);

			try (MockedStatic<ItemResponseDto> mockedStatic = mockStatic(ItemResponseDto.class)) {
				given(ItemResponseDto.from(any(Item.class))).willReturn(SharedData.ITEM_RESPONSE_DTO);

				// when
				Page<ItemResponseDto> actualResult = itemService.getItemList(2, 10, 1000, 5000, SharedData.ITEM_NAME2);

				// then
				verify(itemRepository).findHighestItemPrice();
				verify(itemRepository).findByPriceWithItemName(eq(pageable), anyInt(), anyInt(), anyString());

				assertThat(actualResult).isNotNull();
				ItemResponseDto dto = actualResult.getContent().get(0);
				assertThat(dto.getItemName()).isEqualTo(SharedData.ITEM2.getItemName());
				assertThat(dto.getRemainingQuantity()).isEqualTo(SharedData.ITEM2.getRemainingQuantity());
				assertThat(dto.getPrice()).isEqualTo(SharedData.ITEM2.getPrice());
				assertThat(dto.getExplanation()).isEqualTo(SharedData.ITEM2.getExplanation());
			}
		}

		@Test
		@DisplayName("success: 가격 범위 재설정 - minPrice가 Integer.Max_VALUE인 경우")
		void getItemListWhenMinPriceIsMaxValue() {
			// given
			Pageable pageable = PageRequest.of(1, 10);
			when(itemRepository.findHighestItemPrice()).thenReturn(Optional.of(5000));
			List<Item> itemList = Collections.singletonList(SharedData.ITEM2);
			Page<Item> itemPage = new PageImpl<>(itemList, pageable, itemList.size());
			// minPrice가 Integer.MAX_VALUE일 경우 가격 범위를 '아이템 최고가 ~ Integer.MAX_VALUE'로 재설정
			when(itemRepository.findByPriceWithItemName(pageable, 5000, Integer.MAX_VALUE,
				SharedData.ITEM_NAME2)).thenReturn(itemPage);

			try (MockedStatic<ItemResponseDto> mockedStatic = mockStatic(ItemResponseDto.class)) {
				given(ItemResponseDto.from(any(Item.class))).willReturn(SharedData.ITEM_RESPONSE_DTO);

				// when
				Page<ItemResponseDto> actualResult = itemService.getItemList(2, 10, Integer.MAX_VALUE, 5000,
					SharedData.ITEM_NAME2);

				// then
				verify(itemRepository).findHighestItemPrice();
				verify(itemRepository).findByPriceWithItemName(pageable, 5000, Integer.MAX_VALUE,
					SharedData.ITEM_NAME2);
			}
		}

		@Test
		@DisplayName("success: maxPrice 재설정 - maxPrice가 minPrice보다 작을 경우")
		void getItemListWhenMaxPriceLessThanOrEqualToMinPrice() {
			// given
			Pageable pageable = PageRequest.of(1, 10);
			when(itemRepository.findHighestItemPrice()).thenReturn(Optional.of(6000));
			List<Item> itemList = Collections.singletonList(SharedData.ITEM2);
			Page<Item> itemPage = new PageImpl<>(itemList, pageable, itemList.size());

			when(itemRepository.findByPriceWithItemName(pageable, 1000, 6000, SharedData.ITEM_NAME2)).thenReturn(
				itemPage);

			try (MockedStatic<ItemResponseDto> mockedStatic = mockStatic(ItemResponseDto.class)) {
				given(ItemResponseDto.from(any(Item.class))).willReturn(SharedData.ITEM_RESPONSE_DTO);

				// when
				// maxPrice가 minPrice보다 작음
				Page<ItemResponseDto> actualResult = itemService.getItemList(2, 10, 5000, 1000, SharedData.ITEM_NAME2);

				// then
				verify(itemRepository).findHighestItemPrice();
				verify(itemRepository).findByPriceWithItemName(pageable, 1000, 6000, SharedData.ITEM_NAME2);
			}
		}
	}
}
package excluz.excluz.domain.store.store.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.domain.store.item.dto.response.ItemResponseDto;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.store.store.dto.request.StoreDeleteRequestDto;
import excluz.excluz.domain.store.store.dto.request.StoreRequestDto;
import excluz.excluz.domain.store.store.dto.request.StoreUpdateRequestDto;
import excluz.excluz.domain.store.store.dto.response.StoreDetailResponseDto;
import excluz.excluz.domain.store.store.dto.response.StoreNameResponseDto;
import excluz.excluz.domain.store.store.dto.response.StoreResponseDto;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.service.StreamerService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final ItemRepository itemRepository;
	private final StreamerService streamerService;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public StoreResponseDto createStore(StoreRequestDto storeRequestDto, Integer streamerId) {
		Streamer streamer = getStreamerByIdAndNotDeleted(streamerId);

		Store store = Store.builder()
			.streamer(streamer)
			.address(storeRequestDto.getAddress())
			.storeName(storeRequestDto.getStoreName())
			.registrationNumber(storeRequestDto.getRegistrationNumber())
			.build();

		return StoreResponseDto.from(storeRepository.save(store));
	}

	@Transactional
	public void deleteStore(StoreDeleteRequestDto deleteRequestDto, Integer streamerId, Integer storeId) {
		Streamer streamer = getStreamerByIdAndNotDeleted(streamerId);

		if (!passwordEncoder.matches(deleteRequestDto.getPassword(), streamer.getPassword())) {
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		Store store = storeRepository.findById(storeId).orElseThrow(
			() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND)
		);

		if (store.isDeleted()) {
			throw new NotFoundException(ErrorCode.STORE_NOT_FOUND);
		}

		store.updateIsDeleted(true);
	}

	@Transactional
	public StoreResponseDto updateStore(Integer userId, Integer storeId, StoreUpdateRequestDto requestDto) {
		Store store = getStoreByIdAndNotDeleted(storeId);

		// 스토어 주인 검증 로직
		if (!store.getStreamer().getId().equals(userId)) {
			throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
		}

		store.updateStore(requestDto.getAddress(),
			requestDto.getStoreName(),
			requestDto.getRegistrationNumber());

		return StoreResponseDto.from(store);
	}

	@Transactional(readOnly = true)
	public Page<StoreNameResponseDto> getStoreList(String storeName, int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

		Page<Store> storeList = storeRepository.findByStoreName(pageable, storeName);

		return storeList.map(store -> new StoreNameResponseDto(store.getStoreName(), store.getId()));
	}

	@Transactional(readOnly = true)
	public StoreDetailResponseDto getStoreById(Integer storeId, int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
		Streamer streamer = storeRepository.findStreamerWithStore(storeId).orElseThrow(
			() -> new NotFoundException(ErrorCode.USER_NOT_FOUND)
		);

		Store store = getStoreByIdAndNotDeleted(storeId);

		Page<Item> itemList = itemRepository.findByStoreId(storeId, pageable);
		List<ItemResponseDto> itemResponseList = itemList.stream().map(ItemResponseDto::from).toList();
		Page<ItemResponseDto> itemResponsePage = new PageImpl<>(itemResponseList, pageable, itemList.getTotalElements());

		return StoreDetailResponseDto.of(streamer.getNickName(), store, itemResponsePage);
	}

	@Transactional(readOnly = true)
	public StoreDetailResponseDto getOwnedStoreById(Integer streamerId, int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
		// 스트리머 삭제 회원 여부 확인
		Streamer streamer = getStreamerByIdAndNotDeleted(streamerId);

		// 스트리머 본인 스토어 조회
		Store store = storeRepository.findStoreWithStreamer(streamerId).orElseThrow(
			() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND)
		);

		if (store.isDeleted()) {
			throw new NotFoundException(ErrorCode.STORE_NOT_FOUND);
		}

		Page<Item> itemList = itemRepository.findByStoreId(store.getId(), pageable);

		return StoreDetailResponseDto.of(streamer.getNickName(), store, itemList.map(item -> new ItemResponseDto()));
	}

	// 탈퇴하지 않은 스트리머만 반환
	private Streamer getStreamerByIdAndNotDeleted(Integer streamerId) {
		Streamer streamer = streamerService.findStreamerById(streamerId);

		if (streamer.isDeleted()) {
			throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
		}
		return streamer;
	}

	// 삭제되지 않은 스토어만 반환
	private Store getStoreByIdAndNotDeleted(Integer storeId) {
		Store store = storeRepository.findById(storeId).orElseThrow(
			() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND)
		);

		if (store.isDeleted()) {
			throw new NotFoundException(ErrorCode.STORE_NOT_FOUND);
		}
		return store;
	}
}

package excluz.excluz.domain.store.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.store.store.dto.request.StoreRequestDto;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.service.StreamerService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final StreamerService streamerService;

	@Transactional
	public void createStore(StoreRequestDto storeRequestDto, Integer streamerId) {
		Streamer streamer = streamerService.findStreamerById(streamerId);

		// 삭제된 유저는 스토어 생성 불가
		if (streamer.isDeleted()) {
			throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
		}

		Store store = Store.builder()
			.streamer(streamer)
			.address(storeRequestDto.getAddress())
			.storeName(storeRequestDto.getStoreName())
			.registrationNumber(storeRequestDto.getRegistrationNumber())
			.build();

		storeRepository.save(store);
	}
}

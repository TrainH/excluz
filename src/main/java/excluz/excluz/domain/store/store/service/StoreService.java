package excluz.excluz.domain.store.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.domain.store.store.dto.request.StoreRequestDto;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final StreamerService streamerService;

	@Transactional
	public void createStore(StoreRequestDto storeRequestDto, Integer streamerId) {
		Streamer streamer = findStreamerById(streamerId);
		Store store = Store.builder()
			.streamer(streamer)
			.address(storeRequestDto.getAddress())
			.storeName(storeRequestDto.getStoreName())
			.registrationNumber(storeRequestDto.getRegistrationNumber())
			.build();

		storeRepository.save(store);
	}
}

package excluz.excluz.domain.store.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.auth.util.PasswordEncoder;
import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.store.store.dto.request.StoreDeleteRequestDto;
import excluz.excluz.domain.store.store.dto.request.StoreRequestDto;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final StreamerService streamerService;
	private final PasswordEncoder passwordEncoder;

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

	@Transactional
	public void deleteStore(@Valid StoreDeleteRequestDto deleteRequestDto, Integer streamerId) {
		Streamer streamer = findStreamerById(streamerId);

		if(!passwordEncoder.matches(deleteRequestDto.getPassword(), streamer.getPassword())) {
			throw new BadRequestException(ErrorCode.INVALID_PASSWORD); /*TODO 추후에 수정 예정*/
		}

		streamer.updateStreamerStatus(true);
	}
}

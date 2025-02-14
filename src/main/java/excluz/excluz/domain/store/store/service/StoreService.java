package excluz.excluz.domain.store.store.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.domain.store.store.dto.request.StoreDeleteRequestDto;
import excluz.excluz.domain.store.store.dto.request.StoreRequestDto;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.service.StreamerService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final StreamerService streamerService;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void createStore(StoreRequestDto storeRequestDto, Integer streamerId) {
		Streamer streamer = getStreamerByIdAndNotDeleted(streamerId);

		Store store = Store.builder()
			.streamer(streamer)
			.address(storeRequestDto.getAddress())
			.storeName(storeRequestDto.getStoreName())
			.registrationNumber(storeRequestDto.getRegistrationNumber())
			.build();

		storeRepository.save(store);
	}

	@Transactional
	public void deleteStore(StoreDeleteRequestDto deleteRequestDto, Integer streamerId) {
		Streamer streamer = getStreamerByIdAndNotDeleted(streamerId);

		if(!passwordEncoder.matches(deleteRequestDto.getPassword(), streamer.getPassword())) {
			throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH);
		}

		streamer.updateStreamerStatus(true);
	}

	// 삭제 되지 않은 유저만 반환
	private Streamer getStreamerByIdAndNotDeleted(Integer streamerId) {
		Streamer streamer = streamerService.findStreamerById(streamerId);

		if (streamer.isDeleted()) {
			throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
		}
		return streamer;
	}
}

package excluz.excluz.domain.store.storeRevenue.service;

import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.StoreRevenue;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.store.storeRevenue.dto.response.StoreRevenueResponseDto;
import excluz.excluz.domain.store.storeRevenue.repository.StoreRevenueRepository;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreRevenueService {
    private final StoreRepository storeRepository;
    private final StoreRevenueRepository storeRevenueRepository;

    @Transactional(readOnly = true)
    public Page<StoreRevenueResponseDto> getStoreRevenueList(Integer userOrStreamerId, UserRole userRole, Pageable pageable) {

        Store store = storeRepository.findStoreWithStreamer(userOrStreamerId).orElseThrow(
                () -> new NotFoundException(ErrorCode.STORE_NOT_FOUND)
        );
        return storeRevenueRepository.findByStoreId(store.getId(), pageable);
    }
}


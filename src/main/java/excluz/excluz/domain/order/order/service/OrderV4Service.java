package excluz.excluz.domain.order.order.service;

import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import excluz.excluz.domain.order.order.repository.OrderV2Repository;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderV4Service {
    private final OrderV2Repository orderV2Repository;

    @Cacheable(value = "ORDER_LIST_REDIS", cacheManager = "redisCacheManager",
               key = "#userOrStreamerId + '_' + #userRole + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrderList(Integer userOrStreamerId, UserRole userRole, Pageable pageable) {

        return switch (userRole) {
            case CUSTOMER -> orderV2Repository.findByUserId(userOrStreamerId, pageable);
            case STREAMER -> orderV2Repository.findByStreamerId(userOrStreamerId, pageable);
            default -> throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
        };
    }
}

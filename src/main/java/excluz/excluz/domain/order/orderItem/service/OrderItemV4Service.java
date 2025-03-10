package excluz.excluz.domain.order.orderItem.service;

import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.order.orderItem.dto.response.OrderItemResponseDto;
import excluz.excluz.domain.order.orderItem.repository.OrderItemV2Repository;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemV4Service {
    private final OrderItemV2Repository orderItemV2Repository;

    @Cacheable(value = "ORDER_ITEM_LIST_REDIS", cacheManager = "redisCacheManager",
            key = "#userOrStreamerId + '_' + #userRole + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<OrderItemResponseDto> getOrderItemList(Integer userOrStreamerId, UserRole userRole, Pageable pageable) {
        return switch (userRole) {
            case CUSTOMER -> orderItemV2Repository.findByUserId(userOrStreamerId, pageable);
            case STREAMER -> orderItemV2Repository.findByStreamerId(userOrStreamerId, pageable);
            default -> throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
        };
    }
}
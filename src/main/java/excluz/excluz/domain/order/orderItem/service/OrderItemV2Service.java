package excluz.excluz.domain.order.orderItem.service;

import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.order.orderItem.dto.response.OrderItemResponseDto;
import excluz.excluz.domain.order.orderItem.repository.OrderItemV2Repository;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemV2Service {
    private final OrderItemV2Repository orderItemV2Repository;

    @Transactional(readOnly = true)
    public Page<OrderItemResponseDto> getOrderItemList(Integer userOrStreamerId, UserRole userRole, Pageable pageable) {

        if (userRole.equals(UserRole.CUSTOMER)) {
            return orderItemV2Repository.findByUserId(userOrStreamerId, pageable);
        }

        if (userRole.equals(UserRole.STREAMER)) {
            return orderItemV2Repository.findByStreamerId(userOrStreamerId, pageable);
        }
        throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
    }
}

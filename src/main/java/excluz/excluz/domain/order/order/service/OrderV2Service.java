package excluz.excluz.domain.order.order.service;

import excluz.excluz.common.entity.*;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.order.order.dto.request.OrderUpdateRequestDto;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import excluz.excluz.domain.order.order.enums.OrderStatus;
import excluz.excluz.domain.order.order.repository.OrderRepository;
import excluz.excluz.domain.order.order.repository.OrderV2Repository;
import excluz.excluz.domain.order.orderItem.repository.OrderItemRepository;
import excluz.excluz.domain.point.point.repository.PointRepository;
import excluz.excluz.domain.point.pointTransaction.enums.TransactionType;
import excluz.excluz.domain.point.pointTransaction.repository.PointTransactionRepository;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderV2Service {

    private final OrderV2Repository orderV2Repository;


    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrderList(Integer userOrStreamerId, UserRole userRole, Pageable pageable) {

        if (userRole.equals(UserRole.CUSTOMER)) {
            return orderV2Repository.findByUserId(userOrStreamerId, pageable);
        }

        if (userRole.equals(UserRole.STREAMER)) {
            return orderV2Repository.findByStreamerId(userOrStreamerId, pageable);
        }

        throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
    }

}

package excluz.excluz.domain.order.order.service;

import excluz.excluz.common.entity.Order;
import excluz.excluz.common.entity.Streamer;
import excluz.excluz.common.entity.User;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.order.order.dto.request.OrderUpdateRequestDto;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import excluz.excluz.domain.order.order.enums.OrderStatus;
import excluz.excluz.domain.order.order.repository.OrderRepository;
import excluz.excluz.domain.order.orderItem.repository.OrderItemRepository;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import excluz.excluz.domain.user.enums.UserRole;
import excluz.excluz.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final StreamerRepository streamerRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrderList(Integer userOrStreamerId, String userRole, Pageable pageable) {

        // 로그인한 UserRole이 CUSTOMER이면 user 불러오기
        if (userRole.equals(UserRole.CUSTOMER.getRole())) {
            return orderRepository.findByUserId(userOrStreamerId, pageable).map(OrderResponseDto::from);
        }

        if (userRole.equals(UserRole.STREAMER.getRole())) {
            return orderRepository.findByStreamerId(userOrStreamerId, pageable);
        }

        throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Integer userOrStreamerId, String userRole, Integer orderId) {
        if (userRole.equals(UserRole.CUSTOMER.getRole())) {
            Order order = orderRepository.findByIdAndUserId(orderId, userOrStreamerId).orElseThrow(
                    () -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND)
            );
            return OrderResponseDto.from(order);
        }

        if (userRole.equals(UserRole.STREAMER.getRole())) {
            Order order = orderRepository.findByIdAndStreamerId(orderId, userOrStreamerId).orElseThrow(
                    () -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND)
            );
            return OrderResponseDto.from(order);
        }

        throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
    }

    @Transactional
    public void updateOrder(Integer userOrStreamerId, String userRole, Integer orderId, OrderUpdateRequestDto requestDto) {

        OrderStatus orderStatus = requestDto.getOrderStatus();  // 요청된 상태

        // 주문상태에 따른 변경할 수 있는 주체(CUSTOMER / STREAMER) 검증
        if (!orderStatus.canPerformAction(userRole)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
        }

        if (userRole.equals(UserRole.CUSTOMER.getRole())) {
            Order order = orderRepository.findByIdAndUserId(orderId, userOrStreamerId).orElseThrow(
                    () -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND)
            );
            order.updateWith(requestDto);
            orderRepository.save(order);
            return;
        }

        if (userRole.equals(UserRole.STREAMER.getRole())) {
            Order order = orderRepository.findByIdAndStreamerId(orderId, userOrStreamerId).orElseThrow(
                    () -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND)
            );
            order.updateWith(requestDto);
            orderRepository.save(order);
            return;
        }

        throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
    }
}

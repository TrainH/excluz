package excluz.excluz.domain.order.order.service;

import excluz.excluz.common.entity.*;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.order.order.dto.request.OrderUpdateRequestDto;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import excluz.excluz.domain.order.order.enums.OrderStatus;
import excluz.excluz.domain.order.order.repository.OrderRepository;
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
public class OrderService {
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PointRepository pointRepository;
    private final PointTransactionRepository pointTransactionRepository;

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrderList(Integer userOrStreamerId, UserRole userRole, Pageable pageable) {

        if (userRole.equals(UserRole.CUSTOMER)) {
            return orderRepository.findByUserId(userOrStreamerId, pageable);
        }

        if (userRole.equals(UserRole.STREAMER)) {
            return orderRepository.findByStreamerId(userOrStreamerId, pageable);
        }

        throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Integer userOrStreamerId, UserRole userRole, Integer orderId) {

        if (userRole.equals(UserRole.CUSTOMER)) {
            Order order = orderRepository.findByIdAndUserId(orderId, userOrStreamerId).orElseThrow(
                    () -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND)
            );
            return OrderResponseDto.from(order);
        }

        if (userRole.equals(UserRole.STREAMER)) {
            Order order = orderRepository.findByIdAndStreamerId(orderId, userOrStreamerId).orElseThrow(
                    () -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND)
            );
            return OrderResponseDto.from(order);
        }
        throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
    }

    @Transactional
    public void updateOrder(Integer userOrStreamerId, UserRole userRole, Integer orderId, OrderUpdateRequestDto requestDto) {

        OrderStatus orderStatus = requestDto.getOrderStatus();  // 요청된 상태

        // 주문상태에 따른 변경할 수 있는 주체(CUSTOMER / STREAMER) 검증
        if (!orderStatus.canPerformAction(userRole.getRole())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
        }

        Order order = switch (userRole) {
            case CUSTOMER -> orderRepository.findByIdAndUserId(orderId, userOrStreamerId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));

            case STREAMER -> orderRepository.findByIdAndStreamerId(orderId, userOrStreamerId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));

            default -> throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
        };

        order.updateWith(requestDto.getOrderStatus(), requestDto.getAddress());

        orderRepository.save(order);

        // 주문 취소 일때
        if (order.getOrderStatus() == OrderStatus.CANCELED) {

            List<OrderItem> orderItemList = orderItemRepository.findAllByOrderId(orderId);

            List<Integer> itemIdList = orderItemList.stream()
                    .map(orderItem -> orderItem.getItem().getId()) // 람다식 사용
                    .toList();

            List<Item> itemList = itemRepository.findAllById(itemIdList);


            for (OrderItem orderItem : orderItemList) {
                for (Item item : itemList) {
                    if (item.getId().equals(orderItem.getItem().getId())) {
                        item.addRemainingQuantity(orderItem.getItem_quantity());
                    }
                }
            }

            PointTransaction pointTransaction = pointTransactionRepository.findByOrderId(orderId).orElseThrow(
                    () -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND)
            );

            PointTransaction canceledPointTransaction = PointTransaction.builder()
                    .order(pointTransaction.getOrder())
                    .user(pointTransaction.getUser())
                    .store(pointTransaction.getStore())
                    .transactionType(TransactionType.REFUND)
                    .amount(pointTransaction.getAmount())
                    .build();

            Point streamerPoint = pointRepository.findByUserRoleAndUserOrStreamerId(
                     pointTransaction.getStore().getStreamer().getUserRole(),
                     pointTransaction.getStore().getStreamer().getId())
                    .orElseThrow(
                    () -> new NotFoundException(ErrorCode.POINT_NOT_FOUND)
            );

            Point userPoint = pointRepository.findByUserRoleAndUserOrStreamerId(
                     pointTransaction.getUser().getUserRole(),
                     pointTransaction.getUser().getId())
                    .orElseThrow(
                            () -> new NotFoundException(ErrorCode.POINT_NOT_FOUND)
                    );

            Integer amount = pointTransaction.getAmount();


            // 환불 시, 고객 충전 / 스트리머 차감
            userPoint.chargeAmount(amount);
            streamerPoint.disChargeAmount(amount);

            pointTransactionRepository.save(canceledPointTransaction);
        }
    }
}

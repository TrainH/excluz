package excluz.excluz.domain.order.order.service;

import excluz.excluz.common.entity.Order;
import excluz.excluz.domain.order.order.dto.request.OrderUpdateRequestDto;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import excluz.excluz.domain.order.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrderList(Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderResponseDto::from);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("Order not found")
        );
        return OrderResponseDto.from(order);
    }

    @Transactional
    public void updateOrder(Integer orderId, OrderUpdateRequestDto requestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("Order not found")
        );
        order.updateWith(requestDto);
        orderRepository.save(order);
    }
}

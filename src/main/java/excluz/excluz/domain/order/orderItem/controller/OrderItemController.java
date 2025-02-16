package excluz.excluz.domain.order.orderItem.controller;

import excluz.excluz.domain.order.orderItem.dto.request.OrderItemRequestDto;
import excluz.excluz.domain.order.orderItem.dto.response.OrderItemResponseDto;
import excluz.excluz.domain.order.orderItem.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderItemController {
    private final OrderItemService orderItemService;

    @PostMapping("/order-items")
    public ResponseEntity<String> createOrderItem(@RequestBody List<OrderItemRequestDto> requestList){
        orderItemService.createOrderItemList(requestList);
        return ResponseEntity.ok("주문이 완료 되었습니다.");
    }

    @GetMapping("/order-items")
    public ResponseEntity<Page<OrderItemResponseDto>> getOrderItemList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        Pageable pageable = PageRequest.of(page -1 ,size);

        return ResponseEntity.ok(orderItemService.getOrderItemList(pageable));
    }

    @GetMapping("/order-items/{orderItemId}")
    public ResponseEntity<OrderItemResponseDto> getOrderItem(@PathVariable Integer orderItemId){
        return ResponseEntity.ok(orderItemService.getOrderItem(orderItemId));
    }
}

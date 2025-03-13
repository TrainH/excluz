package excluz.excluz.domain.order.order.controller;

import excluz.excluz.domain.order.order.dto.request.OrderUpdateRequestDto;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import excluz.excluz.domain.order.order.service.OrderService;
import excluz.excluz.domain.user.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import excluz.excluz.auth.util.SecurityContextUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDto>> getOrderList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        Integer userOrStreamerId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("updatedAt")));

        return ResponseEntity.ok(orderService.getOrderList(userOrStreamerId, userRole, pageable));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @PathVariable Integer orderId){

        Integer userOrStreamerId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        return ResponseEntity.ok(orderService.getOrder(userOrStreamerId, userRole, orderId));
    }

    @PatchMapping("/orders/{orderId}")
    public ResponseEntity<String> updateOrder(
            @PathVariable Integer orderId,
            @Valid @RequestBody OrderUpdateRequestDto requestDto) {

        Integer userOrStreamerId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        orderService.updateOrder(userOrStreamerId, userRole, orderId, requestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

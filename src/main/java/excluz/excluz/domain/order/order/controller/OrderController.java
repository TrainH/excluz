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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import excluz.excluz.auth.util.SecurityContextUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDto>> getOrderList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){

        Integer userOrStreamerId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        Pageable pageable = PageRequest.of(page-1, size);

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
        return ResponseEntity.ok("변경이 완료되었습니다.");
    }
}

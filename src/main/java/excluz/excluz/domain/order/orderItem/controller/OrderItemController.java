package excluz.excluz.domain.order.orderItem.controller;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.order.orderItem.dto.request.OrderItemRequestDto;
import excluz.excluz.domain.order.orderItem.dto.response.OrderItemResponseDto;
import excluz.excluz.domain.order.orderItem.service.OrderItemService;
import excluz.excluz.domain.user.enums.UserRole;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderItemController {
    private final OrderItemService orderItemService;

    @PostMapping("/order-items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> createOrderItem(
            @RequestBody List<OrderItemRequestDto> requestList){

        Integer userOrStreamerId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        orderItemService.createOrderItemList(userOrStreamerId, userRole, requestList);
        return ResponseEntity.ok("주문이 완료 되었습니다.");
    }

    @GetMapping("/order-items")
    public ResponseEntity<Page<OrderItemResponseDto>> getOrderItemList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){

        Integer userOrStreamerId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        Pageable pageable = PageRequest.of(page -1 ,size);

        return ResponseEntity.ok(orderItemService.getOrderItemList(userOrStreamerId, userRole, pageable));
    }

    @GetMapping("/order-items/{orderItemId}")
    public ResponseEntity<OrderItemResponseDto> getOrderItem(
            @PathVariable Integer orderItemId){

        Integer userOrStreamerId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        return ResponseEntity.ok(orderItemService.getOrderItem(userOrStreamerId, userRole, orderItemId));
    }
}

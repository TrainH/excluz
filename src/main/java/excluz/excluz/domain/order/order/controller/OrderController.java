package excluz.excluz.domain.order.order.controller;

import excluz.excluz.domain.order.order.dto.request.OrderUpdateRequestDto;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import excluz.excluz.domain.order.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDto>> getOrderList(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        Integer userOrStreamerId = Integer.parseInt(user.getUsername());

        String userRole = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("No role assigned");

        Pageable pageable = PageRequest.of(page-1, size);

        System.out.println(userOrStreamerId);
        System.out.println(userRole);

        return ResponseEntity.ok(orderService.getOrderList(userOrStreamerId, userRole, pageable));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Integer orderId){
        Integer userOrStreamerId = Integer.parseInt(user.getUsername());

        String userRole = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("No role assigned");

        return ResponseEntity.ok(orderService.getOrder(userOrStreamerId, userRole, orderId));
    }

    @PatchMapping("/orders/{orderId}")
    public ResponseEntity<String> updateOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Integer orderId,
            @Valid @RequestBody OrderUpdateRequestDto requestDto) {
        Integer userOrStreamerId = Integer.parseInt(user.getUsername());

        String userRole = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("No role assigned");

        orderService.updateOrder(userOrStreamerId, userRole, orderId, requestDto);
        return ResponseEntity.ok("변경이 완료되었습니다.");
    }
}

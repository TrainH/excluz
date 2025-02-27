package excluz.excluz.domain.order.order.controller;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import excluz.excluz.domain.order.order.service.OrderV2Service;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class OrderV2Controller {
    private final OrderV2Service orderV2Service;

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDto>> getOrderList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        Integer userOrStreamerId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("updatedAt")));

        return ResponseEntity.ok(orderV2Service.getOrderList(userOrStreamerId, userRole, pageable));
    }
}

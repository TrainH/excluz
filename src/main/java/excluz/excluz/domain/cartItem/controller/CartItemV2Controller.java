package excluz.excluz.domain.cartItem.controller;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.cartItem.dto.response.CartItemListResponseDto;
import excluz.excluz.domain.cartItem.service.CartItemV2Service;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/cart-items")
@RequiredArgsConstructor
public class CartItemV2Controller {

    private final CartItemV2Service cartItemV2Service;

    @GetMapping
    public ResponseEntity<CartItemListResponseDto> getCartItemList(
            @RequestParam(defaultValue = "0") int page,     // 기본값 0 (첫 페이지)
            @RequestParam(defaultValue = "10") int size     // 기본값 10 (한 페이지당 10개)
    ) {
        Integer userId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        CartItemListResponseDto response = cartItemV2Service.getCartItemList(userId, userRole, page, size);
        return ResponseEntity.ok(response);
    }
}

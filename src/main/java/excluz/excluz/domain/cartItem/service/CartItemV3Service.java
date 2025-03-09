package excluz.excluz.domain.cartItem.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.CartItem;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.cartItem.dto.response.CartItemListResponseDto;
import excluz.excluz.domain.cartItem.dto.response.GetCartItemResponseDto;
import excluz.excluz.domain.cartItem.repository.CartItemV3Repository;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemV3Service {
    private final CartItemV3Repository cartItemV3Repository;

    // 공통 권한 체크 메서드
    private void checkCustomerRole(UserRole userRole) {
        if (userRole != UserRole.CUSTOMER) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
        }
    }

    // 물품 다건 조회 (인메모리 캐싱 적용)
    @Cacheable(value = "CART_ITEM_LIST_CACHE", key = "#userId + '_' + #userRole + '_' + #page + '_' + #size")
    @Transactional(readOnly = true)
    public CartItemListResponseDto getCartItemList(Integer userId, UserRole userRole, int page, int size) {

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        // 장바구니 이용은 CUSTOMER만 가능
        checkCustomerRole(userRole);

        Page<CartItem> cartItems = cartItemV3Repository.findByUserIdV3(userId, pageable);

        Page<GetCartItemResponseDto> cartItemList = cartItems.map(item -> GetCartItemResponseDto.builder()
            .cartItemId(item.getId())                    // 장바구니 아이템의 ID
            .itemId(item.getItem().getId())              // 연결된 상품의 ID
            .storeId(item.getItem().getStore().getId())  // 해당 상품이 속한 상점의 ID
            .quantity(item.getQuantity())                // 주문 수량
            .itemPrice(item.getItem().getPrice())        // 상품 가격
            .build());

        return new CartItemListResponseDto(cartItemList);
    }
}
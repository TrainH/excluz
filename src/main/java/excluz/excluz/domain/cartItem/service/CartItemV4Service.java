package excluz.excluz.domain.cartItem.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import excluz.excluz.common.entity.CartItem;
import excluz.excluz.common.entity.Item;
import excluz.excluz.common.entity.User;
import excluz.excluz.common.exception.BadRequestException;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.cartItem.dto.request.CreateCartItemRequestDto;
import excluz.excluz.domain.cartItem.dto.request.UpdateCartItemQuantityRequestDto;
import excluz.excluz.domain.cartItem.dto.response.CartItemListResponseDto;
import excluz.excluz.domain.cartItem.dto.response.CreateCartItemResponseDto;
import excluz.excluz.domain.cartItem.dto.response.GetCartItemResponseDto;
import excluz.excluz.domain.cartItem.repository.CartItemRepository;
import excluz.excluz.domain.store.item.repository.ItemRepository;
import excluz.excluz.domain.user.enums.UserRole;
import excluz.excluz.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemV4Service {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    // ✅ Redis 적용

    // 공통 권한 체크 메서드
    private void checkCustomerRole(UserRole userRole) {
        if (userRole != UserRole.CUSTOMER) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
        }
    }

    // 물품 다건 조회
    @Transactional(readOnly = true)
    @Cacheable(value = "CART_ITEM_LIST_REDIS", cacheManager = "redisCacheManager",
        key = "#userId + '_' + #userRole + '_' + #page + '_' + #size")
    public CartItemListResponseDto getCartItemList(Integer userId, UserRole userRole, int page, int size) {

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        // 장바구니 이용은 CUSTOMER만 가능
        checkCustomerRole(userRole);

        Page<CartItem> cartItems = cartItemRepository.findByUserId(userId, pageable);

        Page<GetCartItemResponseDto> cartItemList = cartItems.map(item -> GetCartItemResponseDto.builder()
            .cartItemId(item.getId())                    // 장바구니 아이템의 ID
            .itemId(item.getItem().getId())              // 연결된 상품의 ID
            .storeId(item.getItem().getStore().getId())  // 해당 상품이 속한 상점의 ID
            .quantity(item.getQuantity())                // 주문 수량
            .itemPrice(item.getItem().getPrice())        // 상품 가격
            .build());

        return new CartItemListResponseDto(cartItemList);
    }

    // 물품 추가
    @Transactional
    @CacheEvict(value = "CART_ITEM_LIST_REDIS", cacheManager = "redisCacheManager",
        allEntries = true)
    public CreateCartItemResponseDto addItemToCart(Integer userId, UserRole userRole, CreateCartItemRequestDto requestDto) {
        // 유저 존재 여부 확인
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // 장바구니 이용은 CUSTOMER만 가능
        checkCustomerRole(userRole);

        // 아이템 존재 여부 확인
        Item item = itemRepository.findById(requestDto.getItemId())
            .orElseThrow(() -> new NotFoundException(ErrorCode.ITEM_NOT_FOUND));

        // 기존 장바구니에서 동일한 상품이 있는지 확인
        CartItem cartItem = cartItemRepository.findByUserIdAndItemId(userId, requestDto.getItemId())
            .orElseGet(() -> new CartItem(user, item, 0));

        // 총 수량 계산
        Integer newQuantity = cartItem.getQuantity() + requestDto.getQuantity();

        // 재고 초과 여부 확인
        if (newQuantity > item.getRemainingQuantity()) {
            throw new BadRequestException(ErrorCode.OUT_OF_STOCK);
        }

        // 수량 업데이트 및 저장
        cartItem.updateQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        return CreateCartItemResponseDto.builder()
            .cartItemId(cartItem.getId())
            .itemId(cartItem.getItem().getId())
            .storeId(cartItem.getItem().getStore().getId())
            .quantity(cartItem.getQuantity())
            .itemPrice(cartItem.getItem().getPrice())
            .build();
    }

    // 물품 단건 조회
    // API 경로 일관성을 위해 추가함
    @Transactional(readOnly = true)
    public GetCartItemResponseDto getCartItem(Integer userId, UserRole userRole, Integer cartItemId) {
        // 장바구니 이용은 CUSTOMER만 가능
        checkCustomerRole(userRole);

        // 장바구니에서 해당 아이템 찾기
        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.CART_ITEM_NOT_FOUND));

        return GetCartItemResponseDto.builder()
            .cartItemId(cartItem.getId())
            .itemId(cartItem.getItem().getId())
            .storeId(cartItem.getItem().getStore().getId())
            .quantity(cartItem.getQuantity())
            .itemPrice(cartItem.getItem().getPrice())
            .build();
    }

    // 물품 개수 수정
    @Transactional
    @CacheEvict(value = "CART_ITEM_LIST_REDIS", cacheManager = "redisCacheManager",
        allEntries = true)
    public GetCartItemResponseDto updateCartItemQuantity(
        Integer userId,
        UserRole userRole,
        Integer cartItemId,
        UpdateCartItemQuantityRequestDto requestDto
    ) {
        // 장바구니 이용은 CUSTOMER만 가능
        checkCustomerRole(userRole);

        // 장바구니에서 해당 아이템 찾기
        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.CART_ITEM_NOT_FOUND));

        // 장바구니에 담긴 해당 아이템의 정보 가져오기
        Item item = cartItem.getItem();

        // 사용자가 입력한 개수를 장바구니 속 해당 아이템의 최종 개수로 설정
        Integer updatedQuantity = requestDto.getQuantity();

        // 재고 체크 (요청된 개수가 재고보다 많은 경우 예외 발생)
        if (updatedQuantity > item.getRemainingQuantity()) {
            throw new BadRequestException(ErrorCode.OUT_OF_STOCK);
        }

        // 개수 업데이트 (기존 개수와 관계없이 사용자가 입력한 개수로 설정)
        cartItem.updateQuantity(updatedQuantity);
        cartItemRepository.save(cartItem);

        return GetCartItemResponseDto.builder()
            .cartItemId(cartItem.getId())
            .itemId(cartItem.getItem().getId())
            .storeId(cartItem.getItem().getStore().getId())
            .quantity(cartItem.getQuantity())
            .itemPrice(cartItem.getItem().getPrice())
            .build();
    }

    // 물품 삭제(단건)
    @Transactional
    @CacheEvict(value = "CART_ITEM_LIST_REDIS", cacheManager = "redisCacheManager",
        allEntries = true)
    public void removeCartItem(Integer userId, UserRole userRole, Integer cartItemId) {
        // 장바구니 이용은 CUSTOMER만 가능
        checkCustomerRole(userRole);

        // 장바구니에서 해당 아이템 찾기
        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItemRepository.delete(cartItem);
    }
}
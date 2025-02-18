package excluz.excluz.domain.cartItem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.cartItem.dto.request.CreateCartItemRequestDto;
import excluz.excluz.domain.cartItem.dto.request.UpdateCartItemQuantityRequestDto;
import excluz.excluz.domain.cartItem.dto.response.CartItemListResponseDto;
import excluz.excluz.domain.cartItem.dto.response.CreateCartItemResponseDto;
import excluz.excluz.domain.cartItem.dto.response.GetCartItemResponseDto;
import excluz.excluz.domain.cartItem.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartItemController {
	private final CartItemService cartItemService;

	// 물품 추가
	@PostMapping("/v1/cart-items")
	public ResponseEntity<CreateCartItemResponseDto> addItemToCart(
		@Valid @RequestBody CreateCartItemRequestDto requestDto
	) {
		Integer userId = SecurityContextUtil.getUserId();

		// 서비스단으로 userId와 리퀘스트 정보 넘기기
		CreateCartItemResponseDto response = cartItemService.addItemToCart(userId, requestDto);

		// HTTP 상태 코드 201(create)와 함께 CreateCartItemResponseDto 응답
		return ResponseEntity.status(201).body(response);
	}

	// 물품 단건 조회
	@GetMapping("/v1/cart-items/{cartItemId}")
	public ResponseEntity<GetCartItemResponseDto> getCartItem(
		@PathVariable(name = "cartItemId") Integer cartItemId
	) {
		Integer userId = SecurityContextUtil.getUserId();

		GetCartItemResponseDto response = cartItemService.getCartItem(userId, cartItemId);
		return ResponseEntity.ok(response);
	}

	// 물품 다건 조회
	@GetMapping("/v1/cart-items")
	public ResponseEntity<CartItemListResponseDto> getCartItemList() {
		Integer userId = SecurityContextUtil.getUserId();

		CartItemListResponseDto response = cartItemService.getCartItemList(userId);
		return ResponseEntity.ok(response);
	}

	// 물품 개수 수정
	@PatchMapping("/v1/cart-items/{cartItemId}")
	public ResponseEntity<GetCartItemResponseDto> updateCartItemQuantity(
		@PathVariable(name = "cartItemId") Integer cartItemId,
		@Valid @RequestBody UpdateCartItemQuantityRequestDto requestDto
	) {
		Integer userId = SecurityContextUtil.getUserId();

		GetCartItemResponseDto response = cartItemService.updateCartItemQuantity(userId, cartItemId, requestDto);
		return ResponseEntity.ok(response);
	}

	// 물품 삭제(단건)
	@DeleteMapping("/v1/cart-items/{cartItemId}")
	public ResponseEntity<Void> removeCartItem(
		@PathVariable(name = "cartItemId") Integer cartItemId
	) {
		Integer userId = SecurityContextUtil.getUserId();

		// cartItemId를 서비스 단으로 넘겨서 검증 및 삭제 진행
		cartItemService.removeCartItem(userId, cartItemId);

		// HTTP 상태 코드 204(No Content) 반환
		return ResponseEntity.noContent().build();
	}
}

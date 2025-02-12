package excluz.excluz.domain.cartItem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.domain.cartItem.dto.request.CreateCartItemRequestDto;
import excluz.excluz.domain.cartItem.dto.response.CreateCartItemResponseDto;
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
		@RequestHeader Integer userId,
		@Valid @RequestBody CreateCartItemRequestDto requestDto
	) {
		// 서비스단으로 userId와 리퀘스트 정보 넘기기
		CreateCartItemResponseDto response = cartItemService.addItemToCart(userId, requestDto);

		// HTTP 상태 코드 201(create)와 함께 CreateCartItemResponseDto 응답
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}

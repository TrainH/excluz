package excluz.excluz.domain.store.store.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.domain.store.store.dto.request.StoreRequestDto;
import excluz.excluz.domain.store.store.service.StoreService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreV1Controller {

	private final StoreService storeService;

	@PostMapping()
	public ResponseEntity<Void> createStore(
		/*TODO JWT 토큰에서 streamerId 정보 추출. @AuthenticationPrincipal AuthUser user 쓰시려나?*/
		HttpServletRequest request,
		@Valid @RequestBody StoreRequestDto storeRequestDto
	) {

		/* TODO 인증/인가 구현방식에 따라 Id 정보 추출 로직 수정 예정 */
		storeService.createStore(storeRequestDto, (Integer)request.getAttribute("StreamerId"));

		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}

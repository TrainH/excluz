package excluz.excluz.domain.point.pointTransaction.controller;

import excluz.excluz.domain.point.pointTransaction.dto.response.PointTransactionResponseDto;
import excluz.excluz.domain.point.pointTransaction.service.PointTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PointTransactionContoller {
    private final PointTransactionService pointTransactionService;

    @GetMapping("/points/transactions")
    public ResponseEntity<Page<PointTransactionResponseDto>> getPointTransactionList(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Integer userOrStreamerId = Integer.parseInt(user.getUsername());

        String userRole = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("No role assigned");

        Pageable pageable = PageRequest.of(page-1, size);

        return ResponseEntity.ok(
                pointTransactionService
                        .getPointTransactionList(userOrStreamerId, userRole, pageable));
    }
}

package excluz.excluz.domain.point.pointTransaction.controller;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.point.pointTransaction.dto.response.PointTransactionResponseDto;
import excluz.excluz.domain.point.pointTransaction.service.PointTransactionService;
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
@RequestMapping("/api/v1")
public class PointTransactionContoller {
    private final PointTransactionService pointTransactionService;

    @GetMapping("/points/transactions")
    public ResponseEntity<Page<PointTransactionResponseDto>> getPointTransactionList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Integer userOrStreamerId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        return ResponseEntity.ok(
                pointTransactionService
                        .getPointTransactionList(userOrStreamerId, userRole, pageable));
    }
}

package excluz.excluz.domain.store.storeRevenue.controller;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.store.storeRevenue.dto.response.StoreRevenueResponseDto;
import excluz.excluz.domain.store.storeRevenue.service.StoreRevenueService;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StoreRevenueController {
    private final StoreRevenueService storeRevenueService;

    @GetMapping("/storeRevenues")
    @PreAuthorize("hasRole('STREAMER')")
    public ResponseEntity<Page<StoreRevenueResponseDto>> getAllStoreRevenues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Integer userOrStreamerId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        Pageable pageable = PageRequest.of(page  ,size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(storeRevenueService.getStoreRevenueList(userOrStreamerId, userRole, pageable));
    }

}

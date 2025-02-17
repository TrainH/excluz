package excluz.excluz.domain.point.point.controller;

import excluz.excluz.domain.point.point.dto.request.PointChargeRequestDto;
import excluz.excluz.domain.point.point.dto.response.PointResponseDto;
import excluz.excluz.domain.point.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PointController {

    private final PointService pointService;

    @PostMapping("/points")
    public ResponseEntity<String> chargePoint(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PointChargeRequestDto requestDto
    ) {
        Integer userOrStreamerId = Integer.parseInt(user.getUsername());

        String userRole = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("No role assigned");

        pointService.chargePoint(userOrStreamerId, userRole, requestDto);

        return ResponseEntity.ok("충전되었습니다.");
    }

    @GetMapping("/points/my-point")
    public ResponseEntity<PointResponseDto> getPoint(
            @AuthenticationPrincipal User user
    ){
        Integer userOrStreamerId = Integer.parseInt(user.getUsername());

        String userRole = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("No role assigned");

        return ResponseEntity.ok(pointService.getPoint(userOrStreamerId, userRole));
    }
}

package excluz.excluz.domain.point.point.controller;

import excluz.excluz.auth.util.SecurityContextUtil;
import excluz.excluz.domain.point.point.dto.request.PointChargeRequestDto;
import excluz.excluz.domain.point.point.dto.response.PointResponseDto;
import excluz.excluz.domain.point.point.service.PointService;
import excluz.excluz.domain.user.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PointController {

    private final PointService pointService;

    @PostMapping("/points")
    public ResponseEntity<String> chargePoint(
            @Valid @RequestBody PointChargeRequestDto requestDto
    ) {
        Integer userOrStreamerId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        pointService.chargePoint(userOrStreamerId, userRole, requestDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/points/my-point")
    public ResponseEntity<PointResponseDto> getPoint(
    ){
        Integer userOrStreamerId = SecurityContextUtil.getUserOrStreamerId();
        UserRole userRole = SecurityContextUtil.getUserRole();

        return ResponseEntity.ok(pointService.getPoint(userOrStreamerId, userRole));
    }
}

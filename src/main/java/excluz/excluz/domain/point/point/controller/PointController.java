package excluz.excluz.domain.point.point.controller;

import excluz.excluz.domain.point.point.dto.request.PointChargeRequestDto;
import excluz.excluz.domain.point.point.dto.response.PointResponseDto;
import excluz.excluz.domain.point.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        pointService.chargePoint(requestDto);
        return ResponseEntity.ok("충전되었습니다.");
    }

    @GetMapping("/points/{pointId}")
    public ResponseEntity<PointResponseDto> getPoint(@PathVariable Integer pointId){
        return ResponseEntity.ok(pointService.getPoint(pointId));
    }
}

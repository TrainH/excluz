package excluz.excluz.domain.point.point.service;

import excluz.excluz.common.entity.Point;
import excluz.excluz.common.entity.PointTransaction;
import excluz.excluz.common.entity.User;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.point.point.dto.request.PointChargeRequestDto;
import excluz.excluz.domain.point.point.dto.response.PointResponseDto;
import excluz.excluz.domain.point.point.repository.PointRepository;
import excluz.excluz.domain.point.pointTransaction.enums.TransactionType;
import excluz.excluz.domain.point.pointTransaction.repository.PointTransactionRepository;
import excluz.excluz.domain.user.enums.UserRole;
import excluz.excluz.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointTransactionRepository pointTransactionRepository;

    @Transactional
    public void chargePoint(Integer userOrStreamerId, UserRole userRole, PointChargeRequestDto requestDto) {

        //  포인트 충전은 CUSTOMER만 가능
        if (!userRole.equals(UserRole.CUSTOMER)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
        }

        User user = userRepository.findById(userOrStreamerId).orElseThrow(
                () -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // user의 point가 없는 경우 생성해서 0원 넣기 (초기화)
        Point point = pointRepository.findByUserRoleAndUserOrStreamerId(UserRole.CUSTOMER, userOrStreamerId)
                .orElseGet(() -> new Point(UserRole.CUSTOMER, userOrStreamerId, 0));

        // 충전 금액
        Integer amount = requestDto.getAmount();

        // 포인트 금액 추가
        point.chargeAmount(amount);
        pointRepository.save(point);

        
        // 거래내역 : 충전이기에 order와 store은 null이고 충전
        PointTransaction pointTransaction = new PointTransaction(null, user, null, TransactionType.CHARGE, amount);
        pointTransactionRepository.save(pointTransaction);

    }

    public PointResponseDto getPoint(Integer userOrStreamerId, UserRole userRole) {

        Point point = pointRepository.findByUserRoleAndUserOrStreamerId(userRole, userOrStreamerId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POINT_NOT_FOUND));

        return PointResponseDto.from(point);
    }
}

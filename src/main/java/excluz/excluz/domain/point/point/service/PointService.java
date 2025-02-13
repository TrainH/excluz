package excluz.excluz.domain.point.point.service;

import excluz.excluz.common.entity.Point;
import excluz.excluz.common.entity.PointTransaction;
import excluz.excluz.common.entity.User;
import excluz.excluz.common.exception.ForbiddenException;
import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.point.point.dto.request.PointChargeRequestDto;
import excluz.excluz.domain.point.point.repository.PointRepository;
import excluz.excluz.domain.point.pointTransaction.enums.TransactionType;
import excluz.excluz.domain.point.pointTransaction.repository.PointTransactionRepository;
import excluz.excluz.domain.store.store.repository.StoreRepository;
import excluz.excluz.domain.streamer.repository.StreamerRepository;
import excluz.excluz.domain.user.enums.UserRole;
import excluz.excluz.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PointRepository pointRepository;
    private final PointTransactionRepository pointTransactionRepository;

    @Transactional
    public void chargePoint(PointChargeRequestDto requestDto) {

        Integer userId = 1; // 이후 user 완성되면 수정
        UserRole userRole = UserRole.valueOf("CUSTOMER"); // 이후 user 완성되면 수정

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorCode.USER_NOT_FOUND));


        // CUSTOMER 인 경우만 충전
        if (!userRole.equals(UserRole.CUSTOMER)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_USER_ACCESS);
        }


        Integer amount = requestDto.amount();


        // user의 point가 없는 경우 생성해서 0원 넣기
        Point point = pointRepository.findByUserOrStreamerId(userId)
                .orElseGet(() -> new Point(UserRole.CUSTOMER, userId, 0));

        // 포인트 금액 추가
        point.chargeAmount(amount);

        // 충전이기에 order와 store은 null
        PointTransaction pointTransaction = new PointTransaction(null, user, null, TransactionType.CHARGE, amount);

        pointTransactionRepository.save(pointTransaction);

        pointRepository.save(point);
    }

}

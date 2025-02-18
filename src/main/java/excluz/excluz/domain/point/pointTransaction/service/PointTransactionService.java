package excluz.excluz.domain.point.pointTransaction.service;

import excluz.excluz.common.exception.NotFoundException;
import excluz.excluz.common.exception.error.ErrorCode;
import excluz.excluz.domain.point.pointTransaction.dto.response.PointTransactionResponseDto;
import excluz.excluz.domain.point.pointTransaction.repository.PointTransactionRepository;
import excluz.excluz.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointTransactionService {

    private final PointTransactionRepository pointTransactionRepository;

    @Transactional(readOnly = true)
    public Page<PointTransactionResponseDto> getPointTransactionList(
                                                                    Integer userOrStreamerId,
                                                                    String userRole,
                                                                    Pageable pageable)

    {
        if (userRole.equals(UserRole.CUSTOMER.getRole())){
            return pointTransactionRepository.findAllByUserId(userOrStreamerId, pageable)
                    .map(PointTransactionResponseDto::from);
        }

        if (userRole.equals(UserRole.STREAMER.getRole())){
            return pointTransactionRepository.finAllByStreamerId(userOrStreamerId, pageable)
                    .map(PointTransactionResponseDto::from);
        }

        throw new NotFoundException(ErrorCode.FORBIDDEN_USER_ACCESS) ;
    }
}

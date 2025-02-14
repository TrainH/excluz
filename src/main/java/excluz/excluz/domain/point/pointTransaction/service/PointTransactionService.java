package excluz.excluz.domain.point.pointTransaction.service;

import excluz.excluz.domain.point.pointTransaction.dto.response.PointTransactionResponseDto;
import excluz.excluz.domain.point.pointTransaction.repository.PointTransactionRepository;
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
    public Page<PointTransactionResponseDto> getPointTransactionList(Pageable pageable) {
        return pointTransactionRepository.findAllWithUserAndStoreAndStreamer(pageable)
                .map(PointTransactionResponseDto::from);
    }
}

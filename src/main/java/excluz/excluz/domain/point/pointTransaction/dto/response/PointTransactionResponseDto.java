package excluz.excluz.domain.point.pointTransaction.dto.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import excluz.excluz.common.entity.PointTransaction;
import excluz.excluz.domain.point.pointTransaction.enums.TransactionType;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PointTransactionResponseDto {
    private String userNickName;
    private String streamerNickName;
    private TransactionType transactionType;
    private Integer amount;
    private LocalDateTime createdAt;

    @Builder
    public PointTransactionResponseDto(String userNickName,
                                       String streamerNickName,
                                       TransactionType transactionType,
                                       Integer amount,
                                       LocalDateTime createdAt) {
        this.userNickName = userNickName;
        this.streamerNickName = streamerNickName;
        this.transactionType = transactionType;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public static PointTransactionResponseDto from(PointTransaction pointTransaction) {
        return PointTransactionResponseDto.builder()
                .userNickName(pointTransaction.getUser().getNickName())
                .streamerNickName(pointTransaction.getStore() != null
                                  && pointTransaction.getStore().getStreamer() != null
                                    ? pointTransaction.getStore().getStreamer().getNickName()
                                    : null)
                .transactionType(pointTransaction.getTransactionType())
                .amount(pointTransaction.getAmount())
                .createdAt(pointTransaction.getCreatedAt())
                .build();
    }
}

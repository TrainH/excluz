package excluz.excluz.common.entity;

import excluz.excluz.domain.point.pointTransaction.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "point_transactions")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class PointTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "order_id", nullable = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @JoinColumn(name = "store_id", nullable = true)
    private Store store;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Integer amount;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @Builder
    public PointTransaction(Order order,
                            User user,
                            Store store,
                            TransactionType transactionType,
                            Integer amount) {
        this.order = order;
        this.user = user;
        this.store = store;
        this.transactionType = transactionType;
        this.amount = amount;
    }
}

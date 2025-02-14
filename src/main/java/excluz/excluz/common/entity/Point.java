package excluz.excluz.common.entity;

import excluz.excluz.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "points")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(nullable = false)
    private Integer userOrStreamerId;

    @Column(nullable = false)
    private Integer amount;

    @LastModifiedDate
    @Column(nullable = false)
    protected LocalDateTime updatedAt;

    public Point(UserRole userRole, Integer userOrStreamerId, Integer amount) {
        this.userRole = userRole;
        this.userOrStreamerId = userOrStreamerId;
        this.amount = amount;
    }

    public void chargeAmount(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다."); // 임시 예외처리
        }
        this.amount += amount;
    }

    public void disChargeAmount(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("차감 금액은 0보다 커야 합니다.");// 임시 예외처리
        }
        if (this.amount < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다."); // 임시 예외처리
        }
        this.amount -= amount;
    }
}

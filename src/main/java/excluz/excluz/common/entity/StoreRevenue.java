package excluz.excluz.common.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import excluz.excluz.domain.store.storeRevenue.enums.RevenuePeriod;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "store_revenues")
@Entity
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class StoreRevenue {

    @EmbeddedId
    private StoreRevenueId id;

    @Column(name = "total_revenue", nullable = false)
    private Long totalRevenue;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public StoreRevenue(
            Integer storeId,
            RevenuePeriod revenuePeriod,
            LocalDateTime startDatetime,
            LocalDateTime endDatetime,
            Long totalRevenue
    ) {
        this.id = new StoreRevenueId(storeId, revenuePeriod, startDatetime, endDatetime);
        this.totalRevenue = totalRevenue;
    }

    public Integer getStoreId() {
        return id.getStoreId();
    }

    public RevenuePeriod getRevenuePeriod() {
        return id.getRevenuePeriod();
    }

    public LocalDateTime getStartDatetime() {
        return id.getStartDatetime();
    }

    public LocalDateTime getEndDatetime() {
        return id.getEndDatetime();
    }
}
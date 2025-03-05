package excluz.excluz.common.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import excluz.excluz.domain.store.storeRevenue.enums.RevenuePeriod;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRevenueId implements Serializable {

    private Integer storeId;

    @Enumerated(EnumType.STRING)
    private RevenuePeriod revenuePeriod;

    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
}
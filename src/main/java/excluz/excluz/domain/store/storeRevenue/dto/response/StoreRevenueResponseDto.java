package excluz.excluz.domain.store.storeRevenue.dto.response;

import excluz.excluz.common.entity.StoreRevenue;
import excluz.excluz.domain.store.storeRevenue.enums.RevenuePeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class StoreRevenueResponseDto {
    private Integer storeId;
    private RevenuePeriod revenuePeriod;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Long totalRevenue;
    private LocalDateTime createdAt;

    public static StoreRevenueResponseDto from(StoreRevenue storeRevenue) {
        return StoreRevenueResponseDto.builder()
                .storeId(storeRevenue.getStoreId())
                .revenuePeriod(storeRevenue.getRevenuePeriod())
                .startDatetime(storeRevenue.getStartDatetime())
                .endDatetime(storeRevenue.getEndDatetime())
                .totalRevenue(storeRevenue.getTotalRevenue())
                .createdAt(storeRevenue.getCreatedAt())
                .build();
    }
}

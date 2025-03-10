package excluz.excluz.domain.store.storeRevenue.repository;

import excluz.excluz.common.entity.StoreRevenue;
import excluz.excluz.domain.store.storeRevenue.dto.response.StoreRevenueResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRevenueRepository extends JpaRepository<StoreRevenue, Integer> {
    @Query("SELECT new excluz.excluz.domain.store.storeRevenue.dto.response.StoreRevenueResponseDto( " +
            "sr.id.storeId, sr.id.revenuePeriod, sr.id.startDatetime, sr.id.endDatetime, " +
            "sr.totalRevenue, sr.createdAt) " +
            "FROM StoreRevenue sr " +
            "WHERE sr.id.storeId = :storeId")
    Page<StoreRevenueResponseDto> findByStoreId(@Param("storeId") Integer storeId, Pageable pageable);
}

package excluz.excluz.domain.store.storeSettlement.repository;

import static excluz.excluz.common.entity.QStoreSettlement.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import excluz.excluz.domain.store.storeRevenue.enums.RevenuePeriod;
import excluz.excluz.domain.store.storeSettlement.dto.response.QStoreSettlementResponseDto;
import excluz.excluz.domain.store.storeSettlement.dto.response.StoreSettlementResponseDto;

@Repository
public class StoreSettlementV2Repository {

	private final JPAQueryFactory queryFactory;

	public StoreSettlementV2Repository(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	public List<StoreSettlementResponseDto> findByPeriod(Integer storeId, Integer settlementId,
		LocalDateTime startDate, LocalDateTime endDate, RevenuePeriod period, Integer page, Integer size) {
		return queryFactory
			.select(new QStoreSettlementResponseDto(storeSettlement))
			.from(storeSettlement)
			.where(
				storeIdEq(storeId),
				settlementIdEq(settlementId),
				startDateEq(startDate),
				endDateEq(endDate),
				periodEq(period)
			)
			.orderBy(storeSettlement.id.asc())
			.offset(page)
			.limit(size)
			.fetch();
	}

	private BooleanExpression storeIdEq(Integer storeId) {
		return storeId != null ? storeSettlement.storeId.eq(storeId) : null;
	}

	private BooleanExpression settlementIdEq(Integer settlementId) {
		return settlementId != null ? storeSettlement.id.eq(settlementId) : null;
	}

	private BooleanExpression startDateEq(LocalDateTime startDate) {
		return startDate != null ? storeSettlement.startDate.goe(startDate) : null;
	}

	private BooleanExpression endDateEq(LocalDateTime endDate) {
		return endDate != null ? storeSettlement.endDate.loe(endDate) : null;
	}

	private BooleanExpression periodEq(RevenuePeriod period) {
		return period != null ? storeSettlement.settlementPeriod.eq(period) : null;
	}
}

package excluz.excluz.domain.store.item.repository;

import static excluz.excluz.common.entity.QItem.*;

import java.util.List;

import org.springframework.stereotype.Repository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import excluz.excluz.common.entity.Item;

@Repository
public class ItemV2Repository{

	private final JPAQueryFactory queryFactory;

	public ItemV2Repository(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	public List<Item> findByPriceWithItemNameV2(
		Integer minPrice, Integer maxPrice, String itemName, Integer cursor, int limit
	) {

		return queryFactory
			.selectFrom(item)
			.where(
				item.isDeleted.eq(false),
				item.price.goe(minPrice),
				item.price.loe(maxPrice),
				itemNameEq(itemName),
				cursorEq(cursor)
				)
			.orderBy(item.id.desc())
			.limit(limit)
			.fetch();
	}

	private BooleanExpression itemNameEq(String itemName) {
		if (itemName != null && !itemName.trim().isEmpty()) {
			return item.itemName.contains(itemName);
		}
		return null;
	}

	// 커서 값이 있으면 id가 cursor보다 작은 값만 조회 (내림차순 정렬 기준)
	private BooleanExpression cursorEq(Integer cursor) {
		return cursor != null ? item.id.lt(cursor) : null;
	}
}

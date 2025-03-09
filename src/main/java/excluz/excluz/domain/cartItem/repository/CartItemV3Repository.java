package excluz.excluz.domain.cartItem.repository;

import static excluz.excluz.common.entity.QCartItem.*;
import static excluz.excluz.common.entity.QItem.*;
import static excluz.excluz.common.entity.QStore.*;
import static excluz.excluz.common.entity.QUser.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import excluz.excluz.common.entity.CartItem;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CartItemV3Repository {
	private final JPAQueryFactory queryFactory;

	// V3: 인메모리 캐시 | 서비스단에서 적용
	// v2-2 재사용 : fetchJoin() 포함 QueryDSL 방식
	public Page<CartItem> findByUserIdV3(Integer userId, Pageable pageable) {
		// 메인 쿼리 (데이터 조회)
		List<CartItem> cartItems = queryFactory
			.selectFrom(cartItem)
			.leftJoin(cartItem.item, item).fetchJoin()
			.leftJoin(item.store, store).fetchJoin()
			.leftJoin(cartItem.user, user).fetchJoin()
			.where(cartItem.user.id.eq(userId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 전체 개수 조회 (페이징을 위해 필요)
		Long totalCount = Optional.ofNullable(
			queryFactory
				.select(cartItem.count())
				.from(cartItem)
				.where(cartItem.user.id.eq(userId))
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(cartItems, pageable, totalCount);
	}
}
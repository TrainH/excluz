package excluz.excluz.domain.cartItem.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import excluz.excluz.common.entity.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static excluz.excluz.common.entity.QCartItem.cartItem;
import static excluz.excluz.common.entity.QItem.item;
import static excluz.excluz.common.entity.QStore.store;
import static excluz.excluz.common.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class CartItemV2Repository {
    private final JPAQueryFactory queryFactory;

    public Page<CartItem> findByUserId(Integer userId, Pageable pageable) {
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

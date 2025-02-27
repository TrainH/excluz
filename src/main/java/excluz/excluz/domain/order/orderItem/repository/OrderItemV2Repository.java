package excluz.excluz.domain.order.orderItem.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import excluz.excluz.domain.order.orderItem.dto.response.OrderItemResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static excluz.excluz.common.entity.QItem.item;
import static excluz.excluz.common.entity.QOrder.order;
import static excluz.excluz.common.entity.QOrderItem.orderItem;
import static excluz.excluz.common.entity.QStore.store;
import static excluz.excluz.common.entity.QStreamer.streamer;
import static excluz.excluz.common.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class OrderItemV2Repository {
    private final JPAQueryFactory queryFactory;

    public Page<OrderItemResponseDto> findByUserId(Integer userId, Pageable pageable) {
        // 메인 쿼리 (데이터 조회)
        List<OrderItemResponseDto> orderItems = queryFactory
                .select(Projections.constructor(
                        OrderItemResponseDto.class,
                        order.id,
                        orderItem.id,
                        user.nickName,
                        item.id,
                        item.itemName,
                        item.price,
                        orderItem.item_quantity,
                        order.createdAt
                ))
                .from(orderItem)
                .join(orderItem.order, order)
                .join(order.user, user)
                .join(orderItem.item, item)
                .join(item.store, store)
                .join(store.streamer, streamer)
                .where(user.id.eq(userId))
                .orderBy(order.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회 (페이징을 위해 필요)
        Long totalCount = Optional.ofNullable(queryFactory
                .select(orderItem.count())
                .from(orderItem)
                .join(orderItem.order, order)
                .join(order.user, user)
                .join(orderItem.item, item)
                .join(item.store, store)
                .join(store.streamer, streamer)
                .where(user.id.eq(userId))
                .fetchOne()).orElse(0L);

        return new PageImpl<>(orderItems, pageable, totalCount);
    }


    public Page<OrderItemResponseDto> findByStreamerId(Integer streamerId, Pageable pageable) {
        // 데이터 조회 (fetchJoin 적용)
        List<OrderItemResponseDto> content = queryFactory
                .select(Projections.constructor(
                        OrderItemResponseDto.class,
                        order.id,
                        orderItem.id,
                        user.nickName,
                        item.id,
                        item.itemName,
                        item.price,
                        orderItem.item_quantity,  // 변수명 수정: item_quantity → itemQuantity
                        order.createdAt
                ))
                .from(orderItem)
                .join(orderItem.order, order)
                .join(order.user, user)
                .join(orderItem.item, item)
                .join(item.store, store)
                .join(store.streamer, streamer)
                .where(streamer.id.eq(streamerId))
                .orderBy(order.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 조회 (fetchJoin 사용 X)
        long total = Optional.ofNullable(queryFactory
                        .select(orderItem.count())
                        .from(orderItem)
                        .join(orderItem.order, order)
                        .join(order.user, user)
                        .join(orderItem.item, item)
                        .join(item.store, store)
                        .join(store.streamer, streamer)
                        .where(streamer.id.eq(streamerId))
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

}

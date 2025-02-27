package excluz.excluz.domain.order.order.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import excluz.excluz.domain.order.order.dto.response.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static excluz.excluz.common.entity.QOrder.order;
import static excluz.excluz.common.entity.QOrderItem.orderItem;
import static excluz.excluz.common.entity.QItem.item;
import static excluz.excluz.common.entity.QStore.store;
import static excluz.excluz.common.entity.QStreamer.streamer;

@Repository
@RequiredArgsConstructor
public class OrderV2Repository {
    private final JPAQueryFactory queryFactory;

    public Page<OrderResponseDto> findByStreamerId(Integer streamerId, Pageable pageable) {
        // 메인 쿼리 (데이터 조회)
        List<OrderResponseDto> orders = queryFactory
                .select(Projections.constructor(
                        OrderResponseDto.class,
                        order.id,
                        order.orderStatus,
                        order.address,
                        order.updatedAt
                ))
                .from(order)
                .leftJoin(orderItem).on(orderItem.order.eq(order)).fetchJoin()
                .leftJoin(item).on(orderItem.item.eq(item)).fetchJoin()
                .leftJoin(store).on(item.store.eq(store)).fetchJoin()
                .leftJoin(streamer).on(store.streamer.eq(streamer)).fetchJoin()
                .where(streamer.id.eq(streamerId))
                .distinct()
                .orderBy(order.id.desc())
                .offset(pageable.getOffset())   // 페이지 번호 * 페이지 크기
                .limit(pageable.getPageSize())  // 한 페이지당 데이터 개수
                .fetch();

        // 전체 개수 조회 (페이징을 위해 필요)
        Long totalCount = Optional.ofNullable(queryFactory
                        .select(order.count())
                        .from(order)
                        .leftJoin(orderItem).on(orderItem.order.eq(order))
                        .leftJoin(item).on(orderItem.item.eq(item))
                        .leftJoin(store).on(item.store.eq(store))
                        .leftJoin(streamer).on(store.streamer.eq(streamer))
                        .where(streamer.id.eq(streamerId))
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(orders, pageable, totalCount);
    }

    public Page<OrderResponseDto> findByUserId(Integer userId, Pageable pageable) {
        // 데이터 조회
        List<OrderResponseDto> content = queryFactory
                .select(Projections.constructor(
                        OrderResponseDto.class,
                        order.id,
                        order.orderStatus,
                        order.address,
                        order.updatedAt
                ))
                .from(order)
                .where(order.user.id.eq(userId))
                .orderBy(order.id.desc())
                .offset(pageable.getOffset())  // 페이지 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();

        // 총 개수 조회
        long total = Optional.ofNullable(queryFactory
                        .select(order.count())
                        .from(order)
                        .where(order.user.id.eq(userId))
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }
}

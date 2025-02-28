package excluz.excluz.domain.event.event.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import excluz.excluz.common.entity.QEvent;
import excluz.excluz.common.entity.QStore;
import excluz.excluz.common.entity.QStreamer;
import excluz.excluz.domain.event.event.dto.response.EventResponseWithoutEventItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EventV2Repository{

    private final JPAQueryFactory queryFactory;

    public EventV2Repository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Page<EventResponseWithoutEventItemDto> findDtoByStreamerId(Integer streamerId, Pageable pageable) {
        QEvent event = QEvent.event;
        QStore store = QStore.store;
        QStreamer streamer = QStreamer.streamer;

        List<EventResponseWithoutEventItemDto> eventList = queryFactory
                .select(Projections.constructor(
                        EventResponseWithoutEventItemDto.class,
                        event.id,
                        store.id,
                        event.numberOfWinners,
                        event.participantCondition,
                        event.selectionMethod,
                        event.startDatetime,
                        event.endDatetime,
                        event.isCompleted,
                        event.createdAt,
                        event.updatedAt,
                        event.generatedCode
                ))
                .from(event)
                .innerJoin(event.store, store)
                .innerJoin(store.streamer, streamer)
                .where(streamer.id.eq(streamerId))
                .orderBy(event.id.desc()) // 추후 동적 제어 추가 여지
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = Optional.ofNullable(queryFactory
                        .select(Wildcard.count)
                        .from(event)
                        .innerJoin(event.store, store)
                        .innerJoin(store.streamer, streamer)
                        .where(streamer.id.eq(streamerId))
                        .fetchOne())
                .orElse(0L);
        // fetchCount()는 deprecated되어 잘 안 씀

        return new PageImpl<>(eventList, pageable, totalCount);
    }
}

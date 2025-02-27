package excluz.excluz.domain.event.event.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.QEvent;
import excluz.excluz.common.entity.QStore;
import excluz.excluz.common.entity.QStreamer;
import excluz.excluz.domain.event.event.dto.EventResponseWithoutEventItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventV2Repository{

    private final JPAQueryFactory queryFactory;

    public EventV2Repository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Page<EventResponseWithoutEventItemDto> findDtoByStreamerIdUsingQueryDsl(Integer streamerId, Pageable pageable) {
        QEvent event = QEvent.event;
        QStore store = QStore.store;
        QStreamer streamer = QStreamer.streamer;

        JPQLQuery<EventResponseWithoutEventItemDto> query = queryFactory
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
                .leftJoin(event.store, store)
                .leftJoin(store.streamer, streamer)
                .where(streamer.id.eq(streamerId));

        long total = query.fetchCount();

        List<EventResponseWithoutEventItemDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}

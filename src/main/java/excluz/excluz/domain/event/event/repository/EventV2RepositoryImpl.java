package excluz.excluz.domain.event.event.repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.QEvent;
import excluz.excluz.common.entity.QStore;
import excluz.excluz.common.entity.QStreamer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventV2RepositoryImpl implements EventV2RepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public EventV2RepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Event> findByStreamerIdUsingQueryDsl(Integer streamerId, Pageable pageable) {
        QEvent event = QEvent.event;
        QStore store = QStore.store;
        QStreamer streamer = QStreamer.streamer;

        // 쿼리 작성
        JPQLQuery<Event> query = queryFactory
                .selectFrom(event)
                .innerJoin(event.store, store)
                .innerJoin(store.streamer, streamer)
                .where(streamer.id.eq(streamerId));

        // total count
        long total = query.fetchCount();

        // 페이징 처리
        List<Event> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // PageImpl로 감싸서 반환
        return new PageImpl<>(content, pageable, total);
    }

}

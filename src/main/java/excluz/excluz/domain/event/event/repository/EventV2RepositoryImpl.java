//package excluz.excluz.domain.event.event.repository;
//
//import excluz.excluz.common.entity.Event;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class EventV2RepositoryImpl extends QuerydslRepositorySupport implements EventV2RepositoryCustom {
//    public EventV2RepositoryImpl() {
//        super(Event.class);
//    }
//
//    @Override
//    public Page<Event> findByStreamerIdUsingQueryDsl(Integer streamerId, Pageable pageable) {
////        QEvent event = QEvent.event;
////        QStore store = QStore.store;
////        QStreamer streamer = QStreamer.streamer;
////
////        // 쿼리 작성
////        JPQLQuery<Event> query = from(event)
////                .innerJoin(event.store, store)
////                .innerJoin(store.streamer, streamer)
////                .where(streamer.id.eq(streamerId), event.isDeleted.isFalse())
////                .select(event);
////
////        // Page 처리를 위한 QuerydslRepositorySupport의 메서드를 활용
////        return applyPagination(pageable, query);
//    }
//}

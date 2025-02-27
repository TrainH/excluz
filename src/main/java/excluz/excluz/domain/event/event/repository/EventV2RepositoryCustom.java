package excluz.excluz.domain.event.event.repository;

import excluz.excluz.common.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventV2RepositoryCustom {
    Page<Event> findByStreamerIdUsingQueryDsl(Integer streamerId, Pageable pageable);
}

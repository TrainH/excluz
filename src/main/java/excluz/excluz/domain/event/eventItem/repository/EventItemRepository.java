package excluz.excluz.domain.event.eventItem.repository;

import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.EventItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventItemRepository extends JpaRepository<EventItem, Integer> {
    List<EventItem> findByEvent(Event event);
}

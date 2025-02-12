package excluz.excluz.domain.event.eventItem.repository;

import excluz.excluz.common.entity.EventItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventItemRepository extends JpaRepository<EventItem, Integer> {
}

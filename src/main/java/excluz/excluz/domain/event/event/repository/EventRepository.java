package excluz.excluz.domain.event.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import excluz.excluz.common.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}

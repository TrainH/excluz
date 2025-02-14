package excluz.excluz.domain.event.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import excluz.excluz.common.entity.Event;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Optional<Event> findByGeneratedCode(String generatedCode);
}

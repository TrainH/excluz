package excluz.excluz.domain.event.event.repository;

import excluz.excluz.common.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventV2Repository extends JpaRepository<Event, Integer>, EventV2RepositoryCustom {

}

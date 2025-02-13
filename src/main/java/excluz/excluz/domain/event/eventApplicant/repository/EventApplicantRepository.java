package excluz.excluz.domain.event.eventApplicant.repository;

import excluz.excluz.common.entity.EventApplicant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventApplicantRepository extends JpaRepository<EventApplicant, Integer> {
}

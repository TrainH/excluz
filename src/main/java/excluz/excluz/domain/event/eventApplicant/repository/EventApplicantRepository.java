package excluz.excluz.domain.event.eventApplicant.repository;

import excluz.excluz.common.entity.EventApplicant;
import excluz.excluz.common.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventApplicantRepository extends JpaRepository<EventApplicant, Integer> {
    Optional<EventApplicant> findByEventAndEmailAndApplicantPassword(Event event, String email, String applicantPassword);
}

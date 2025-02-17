package excluz.excluz.domain.event.eventApplicant.repository;

import excluz.excluz.common.entity.EventApplicant;
import excluz.excluz.common.entity.Event;
import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventApplicantRepository extends JpaRepository<EventApplicant, Integer> {
    Optional<EventApplicant> findByEventAndEmailAndApplicantPassword(Event event, String email, String applicantPassword);

    List<EventApplicant> findByEvent(Event event);

    int countByEventAndApplicantStatus(Event event, ApplicantStatus status);

    Boolean existsByEventAndEmail(Event event, String email);
}

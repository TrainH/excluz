package excluz.excluz.domain.event.eventApplicant.repository;

import excluz.excluz.common.entity.EventApplicant;
import excluz.excluz.common.entity.Event;
import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventApplicantRepository extends JpaRepository<EventApplicant, Integer> {
    Optional<EventApplicant> findByEventAndEmailAndApplicantPassword(Event event, String email, String applicantPassword);

    List<EventApplicant> findByEvent(Event event);

//    @Lock(LockModeType.PESSIMISTIC_READ)
//    @Query("SELECT COUNT(e) FROM EventApplicant e WHERE e.event = :event AND e.applicantStatus = :status")
//    int countByEventAndApplicantStatus(@Param("event") Event event, @Param("status") ApplicantStatus status);
    int countByEventAndApplicantStatus(Event event, ApplicantStatus status);


    Boolean existsByEventAndEmail(Event event, String email);
}

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

    @Query("SELECT COUNT(ea) FROM EventApplicant ea WHERE ea.event = :event AND ea.applicantStatus = :status")
    int countByEventAndApplicantStatus(Event event, ApplicantStatus status);

    @Query("SELECT COUNT(ea) > 0 FROM EventApplicant ea WHERE ea.event = :event AND ea.email = :email")
    Boolean existsByEventAndEmail(Event event, String email);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT COUNT(ea) > 0 FROM EventApplicant ea WHERE ea.event = :event AND ea.email = :email")
    boolean existsByEventAndEmailForOptimisticLock(@Param("event") Event event, @Param("email") String email);

}

package excluz.excluz.domain.event.event.repository;

import excluz.excluz.common.entity.Store;
import excluz.excluz.domain.event.event.dto.EventResponseDto;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import excluz.excluz.common.entity.Event;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {


    @Query("SELECT new excluz.excluz.domain.event.event.dto.EventResponseDto(" +
            "e.id, e.store.id, e.numberOfWinners, e.participantCondition, e.selectionMethod, e.startDatetime, e.endDatetime, e.isCompleted, " +
            "e.createdAt, e.updatedAt, e.generatedCode, null) " +
            "FROM Event e " +
            "JOIN e.store s " +
            "JOIN s.streamer st " +
            "WHERE st.id = :streamerId")
    Page<EventResponseDto> findByStreamerId(@Param("streamerId") Integer streamerId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.generatedCode = :code")
    Optional<Event> findByGeneratedCode(@Param("code") String generatedCode);
}

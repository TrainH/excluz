package excluz.excluz.domain.event.event.repository;

import excluz.excluz.domain.event.event.dto.response.EventResponseWithoutEventItemDto;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import excluz.excluz.common.entity.Event;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {


    @Query("SELECT new excluz.excluz.domain.event.event.dto.response.EventResponseWithoutEventItemDto(" +
            "e.id, e.store.id, e.numberOfWinners, e.participantCondition, e.selectionMethod, e.startDatetime, e.endDatetime, e.isCompleted, " +
            "e.createdAt, e.updatedAt, e.generatedCode) " +
            "FROM Event e " +
            "JOIN e.store s " +
            "JOIN s.streamer st " +
            "WHERE st.id = :streamerId")
    Page<EventResponseWithoutEventItemDto> findByStreamerId(@Param("streamerId") Integer streamerId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.generatedCode = :code")
    Optional<Event> findByGeneratedCodeForPessimisticLock(@Param("code") String generatedCode);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT e FROM Event e WHERE e.generatedCode = :code")
    Optional<Event> findByGeneratedCodeForOptimisticLock(@Param("code") String generatedCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.generatedCode = :code")
    Optional<Event> findByGeneratedCode(@Param("code") String generatedCode);

    // 조건부 업데이트 쿼리: 현재 당첨자 수가 정원보다 작고, 버전이 일치할 때 업데이트
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Event e SET e.currentWinnerCount = e.currentWinnerCount + 1, e.version = e.version + 1 " +
            "WHERE e.id = :id AND e.currentWinnerCount < e.numberOfWinners")
    int increaseCurrentWinnerCountIfPossible(@Param("id") Integer id,
                                             @Param("version") Integer version);

}

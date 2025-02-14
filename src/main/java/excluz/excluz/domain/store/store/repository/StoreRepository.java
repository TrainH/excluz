package excluz.excluz.domain.store.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import excluz.excluz.common.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Integer> {

	@Query("SELECT s FROM Store s JOIN FETCH s.streamer WHERE s.streamer.id = :streamerId")
	Optional<Store> findStoreWithStreamer(@Param("streamerId") Integer streamerId);
}

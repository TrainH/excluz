package excluz.excluz.domain.store.store.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import excluz.excluz.common.entity.Store;
import excluz.excluz.common.entity.Streamer;

public interface StoreRepository extends JpaRepository<Store, Integer> {

	@Query("SELECT s FROM Store s JOIN FETCH s.streamer WHERE s.streamer.id = :streamerId AND s.isDeleted = false")
	Optional<Store> findStoreWithStreamer(@Param("streamerId") Integer streamerId);

	@Query("SELECT s.streamer FROM Store s WHERE s.id = :storeId")
	Optional<Streamer> findStreamerWithStore(@Param("storeId") Integer storeId);

	@Query("SELECT s FROM Store s " +
		"WHERE (:storeName IS NULL OR s.storeName LIKE CONCAT('%', :storeName, '%')) " +
		"AND s.isDeleted = false " +
		"ORDER BY s.id DESC")
	Page<Store> findByStoreName(Pageable pageable, @Param("storeName") String storeName);

	@Query("SELECT s FROM Store s " +
		"WHERE (s.registrationNumber LIKE :registrationNumber) " +
		"AND s.isDeleted = false")
	Optional<Store> findByRegistrationNumber(@Param("registrationNumber") String registrationNumber);

	@Query("SELECT s FROM Store s " +
		"WHERE (s.streamer.id = :streamerId) " +
		"AND s.isDeleted = false")
	Optional<Store> findStoreByStreamerIdAndNotDeleted(@Param("streamerId") Integer streamerId);
}

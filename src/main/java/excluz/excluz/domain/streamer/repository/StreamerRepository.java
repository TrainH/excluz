package excluz.excluz.domain.streamer.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import excluz.excluz.common.entity.Streamer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public interface StreamerRepository extends JpaRepository<Streamer, Integer> {

	Optional<Streamer> findByEmail(String email);

	@Query("SELECT s FROM Streamer s " +
		"WHERE (:nickName IS NULL OR s.nickName LIKE concat('%', :nickName , '%')) " +
		"AND s.isDeleted=false " +
		"ORDER BY s.id DESC")
	Page<Streamer> findStreamersByNickName(Pageable pageable, @Param("nickName") String nickName);

	@Query("SELECT s FROM Streamer s WHERE (s.phoneNumber LIKE :phoneNumber) AND s.isDeleted=false")
	Optional<Streamer> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

	@Query("SELECT s FROM Streamer s WHERE (s.nickName LIKE :nickName) AND s.isDeleted=false")
	Optional<Streamer> findByNickName(@Param("nickName") String nickName);
}

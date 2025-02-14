package excluz.excluz.domain.streamer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import excluz.excluz.common.entity.Streamer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public interface StreamerRepository extends JpaRepository<Streamer, Integer> {

	Optional<Streamer> findByEmail(String email);
}

package excluz.excluz.domain.streamer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import excluz.excluz.common.entity.Streamer;

public interface StreamerRepository extends JpaRepository<Streamer, Integer> {
}

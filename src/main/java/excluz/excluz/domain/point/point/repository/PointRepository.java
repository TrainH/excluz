package excluz.excluz.domain.point.point.repository;

import excluz.excluz.common.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Integer> {
    @Query("SELECT p FROM Point p WHERE p.userOrStreamerId = :userOrStreamerId")
    Optional<Point> findByUserOrStreamerId(@Param("userOrStreamerId") Integer userOrStreamerId);
}

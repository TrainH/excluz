package excluz.excluz.domain.point.point.repository;

import excluz.excluz.common.entity.Point;
import excluz.excluz.domain.user.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Integer> {

    @Query("SELECT p FROM Point p WHERE p.userRole = :userRole AND p.userOrStreamerId = :userOrStreamerId")
    Optional<Point> findByUserRoleAndUserOrStreamerId(@Param("userRole") UserRole userRole,
                                                      @Param("userOrStreamerId") Integer userOrStreamerId);
}


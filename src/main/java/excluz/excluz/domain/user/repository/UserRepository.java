package excluz.excluz.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import excluz.excluz.common.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}

package excluz.excluz.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import excluz.excluz.common.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);

	@Query("SELECT u FROM User u WHERE (u.nickName LIKE :nickName) AND u.isDeleted=false")
	Optional<User> findByNickName(@Param("nickName") String nickName);

	@Query("SELECT u FROM User u WHERE (u.phoneNumber LIKE :phoneNumber) AND u.isDeleted=false")
	Optional<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}

package excluz.excluz.domain.emailVerification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import excluz.excluz.common.entity.EmailVerify;

public interface EmailVerifyRepository extends JpaRepository<EmailVerify, Integer> {

	Optional<EmailVerify> findByEmail(String email);
}

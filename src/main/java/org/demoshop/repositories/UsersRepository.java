package org.demoshop.repositories;

import org.demoshop.models.ConfirmationCode;
import org.demoshop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findFirstByCodesContains(ConfirmationCode code);
}

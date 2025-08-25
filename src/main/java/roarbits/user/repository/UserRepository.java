package roarbits.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import roarbits.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByUsername(String username);
}
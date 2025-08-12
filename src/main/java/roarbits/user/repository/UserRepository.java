package roarbits.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import roarbits.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    //Optional<User> findById(Long id);
}

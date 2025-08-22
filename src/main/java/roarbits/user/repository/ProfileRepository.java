package roarbits.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import roarbits.user.entity.Profile;
import roarbits.user.entity.User;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser(User user);
    Optional<Profile> findByUser_Id(Long userId);
}

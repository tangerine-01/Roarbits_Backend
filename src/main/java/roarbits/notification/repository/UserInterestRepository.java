package roarbits.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import roarbits.notification.entity.UserInterest;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findByUserId(Long userId);

    Optional<UserInterest> findByUserIdAndInterestTypeAndInterestTargetId(Long userId, String interestType, Long interestTargetId);
}
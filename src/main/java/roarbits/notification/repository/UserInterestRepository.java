package roarbits.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import roarbits.notification.entity.UserInterest;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findByUserId(Long userId);

    Optional<UserInterest> findByUserIdAndInterestTypeAndInterestTargetId(Long userId, String interestType, Long interestTargetId);

    // 소유자 검증/삭제 편의 메서드
    boolean existByIdAndUserId(Long id, Long userId);
    long deleteByIdAndUserId(Long id, Long userId);
}
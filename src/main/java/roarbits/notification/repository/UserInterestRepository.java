package roarbits.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import roarbits.notification.entity.UserInterest;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    boolean existsByUserIdAndSubjectId(Long userId, Long subjectId);
    long deleteByUserIdAndSubjectId(Long userId, Long subjectId);
    List<UserInterest> findAllByUserId(Long userId);
}
package roarbits.coursehistory.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import roarbits.coursehistory.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import roarbits.coursehistory.dto.CourseEnums.Semester;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    List<CourseEntity> findByUserId(Long userId);
    boolean existsByUserIdAndYearAndSemesterAndSubject_Id(
            Long userId, Integer year, Semester semester, Long subjectId);

    Optional<CourseEntity> findByIdAndUserId(Long id, Long userId);

    interface EarnedByCategory {
        String getCategory();
        Long getEarned();
    }

    @Query("""

            select c.category as category, coalesce(sum(c.credit), 0) as earned
            from CourseEntity c
    where c.userId = :userId
    group by c.category
    """)
    List<EarnedByCategory> sumEarnedByCategory(@Param("userId") Long userId);

    @Modifying
    @Transactional
    long deleteByIdAndUserId(Long id, Long userId);
}

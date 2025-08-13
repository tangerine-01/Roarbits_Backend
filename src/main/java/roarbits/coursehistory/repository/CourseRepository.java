package roarbits.coursehistory.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import roarbits.coursehistory.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import roarbits.coursehistory.dto.CourseEnums.Semester;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    List<CourseEntity> findByUserId(Long userId);
    boolean existsByUserIdAndYearAndSemesterAndCourseCode(
            Long userId, Integer year, Semester semester, String courseCode);

    Optional<CourseEntity> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Transactional
    long deleteByIdAndUserId(Long id, Long userId);
}

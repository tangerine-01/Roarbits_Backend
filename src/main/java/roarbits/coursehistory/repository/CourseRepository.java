package roarbits.coursehistory.repository;

import roarbits.coursehistory.entity.CourseEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    List<CourseEntity> findByUserId(Long userId);
}
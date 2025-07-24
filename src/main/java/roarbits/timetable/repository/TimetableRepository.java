package roarbits.timetable.repository;

import roarbits.timetable.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    // 사용자 ID로 시간표 조회
    List<Timetable> findByUserId(Long userId);
}

package roarbits.timetable.repository;

import roarbits.timetable.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    // 사용자별 전체 시간표 조회
    List<Timetable> findAllByUser_UserId(Long userId);

    // 단일 조회
    Optional<Timetable> findByTimetableIdAndUser_UserId(Long timetableId, Long userId);

    // 삭제
    long deleteByTimetableIdAndUser_UserId(Long timetableId, Long userId);
}

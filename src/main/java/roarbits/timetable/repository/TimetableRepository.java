package roarbits.timetable.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roarbits.timetable.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    // 사용자별 전체 시간표 조회
    List<Timetable> findAllByUser_Id(Long userId);

    // 단일 조회
    Optional<Timetable> findByTimetableIdAndUser_Id(Long timetableId, Long userId);

    // 메인 시간표 확인
    Optional<Timetable> findByUser_IdAndIsMainTrue(Long userId);

    @Modifying
    @Query("UPDATE Timetable t SET t.isMain = false WHERE t.user.id = :userId and t.isMain = true")
    int clearMainByUserId(@Param("userId")Long userId);

    boolean existsByUser_IdAndIsMainTrue(Long userId);
    Optional<Timetable> findFirstByUser_IdOrderByTimetableIdDesc(Long userId);

    // 삭제
    long deleteByTimetableIdAndUser_Id(Long timetableId, Long userId);
}

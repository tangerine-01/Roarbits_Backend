package roarbits.timetable.repository;

import org.springframework.stereotype.Repository;
import roarbits.timetable.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import roarbits.timetable.entity.Timetable;

import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    // 시간표 ID로 TimeSlot 목록 조회
    List<TimeSlot> findByTimetable(Timetable timetableId);

    //시간표 전체 삭제 시, 타임슬롯도 함께 삭제
    void deleteByTimetable(Timetable timetable);
}

package roarbits.timetable.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class TimeSlotRequestDto {
    private Long userId; // 사용자 ID
    private String courseName; // 과목명
    private String courseCode; // 과목 코드
    private String professor; // 교수명
    private String dayOfWeek; // 요일 (예: MON, TUE, WED, THU, FRI)
    private String startTime; // 시작 시간 (예: 09:00)
    private String endTime; // 종료 시간 (예: 10:30)
    private String location; // 강의실 위치
    private List<TimeSlotResponseDto> timeSlots;
}

package roarbits.timetable.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotDto {
    private Long subjectId; // 과목 ID
    private String subjectName; // 과목 이름
    private String startTime; // 시간 (예: "9:00")
    private String endTime; // 시간 (예: "10:00")
    private String classroom; // 강의실 (예: "전자관 101호")
    private String professor; // 교수님 이름
    private Integer credit; // 학점
    private String courseType; // 이수구분 (예: "전공선택", "교양필수")
    private String discipline;  // 교양과정 구분 (예: "철학과 역사", "소양")

    private Integer day; // 요일 (0: 월요일, 1: 화요일, ..., 6: 일요일)
}

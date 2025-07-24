package roarbits.timetable.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableResponseDto {
    private Long timetableId; // 시간표 ID
    private Long userId; // 사용자 ID
    private Integer preferCredit; // 선호 학점
    private String preferTime; // 선호 시간대
    private Integer morningClassNum; // 아침 수업 수
    private Integer freePeriodNum; // 자유 시간 수
    private String essentialCourse; // 필수 과목
    private Double graduationRate; // 졸업률

    private List<TimeSlotDto> timeSlots; // 시간표에 포함된 과목들
}

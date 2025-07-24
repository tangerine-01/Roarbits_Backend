package roarbits.timetable.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableRequestDto {
    private Long userId;
    private Integer preferCredit;
    private String preferTime;
    private Integer morningClassNum;
    private Integer freePeriodNum;
    private String essentialCourse;
    private Double graduationRate;

    private List<TimeSlotDto> timeSlots; //시간표에 포함된 과목들
}

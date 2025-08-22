package roarbits.timetable.dto;

import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableRequestDto {
    @PositiveOrZero
    private Integer preferCredit;

    @NotBlank
    private String preferTime;

    @PositiveOrZero
    private Integer morningClassNum;

    @PositiveOrZero
    private Integer freePeriodNum;

    private String category; // 시간표 카테고리 (ex. 전공, 교양, 자유)

    public String getCategory() {
        return category;
    }

    private String essentialCourse;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    private Double graduationRate;

    @NotNull
    @Size(min = 0)
    @Builder.Default
    private List<@Valid TimeSlotDto> timeSlots = List.of(); //시간표에 포함된 과목들
}

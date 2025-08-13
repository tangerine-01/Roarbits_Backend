package roarbits.coursehistory.dto;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import roarbits.coursehistory.dto.CourseEnums.Semester;
import roarbits.coursehistory.dto.CourseEnums.RetakeType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequest {
    @NotNull @Min(2000) @Max(2100)
    private Integer year;

    @NotNull
    private Semester semester;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2,5}\\d{3,5}$", message = "과목 코드 형식의 예시는 다음과 같습니다. (예: CS101)")
    private String courseCode;

    @NotBlank
    @Size(max=100)
    private String courseTitle;

    @NotNull
    @Min(1) @Max(6)
    private int credit;

    @NotNull
    private RetakeType retake;
}

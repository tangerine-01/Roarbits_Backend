package roarbits.coursehistory.dto;
import jakarta.validation.constraints.*;
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

    @NotNull
    @Positive
    private Long subjectId;

    @NotBlank
    @Size(max=100)
    private String courseTitle;

    @NotNull
    @Min(1) @Max(6)
    private int credit;

    @NotBlank
    private String category;

    @NotNull
    private RetakeType retake;
}

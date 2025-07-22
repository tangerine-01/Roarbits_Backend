package roarbits.coursehistory.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String courseCode;

    @NotBlank
    private String courseTitle;

    @Min(1) @Max(6)
    private int credit;

    @NotBlank
    private String semester;
}

package roarbits.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import roarbits.user.entity.GraduationType;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {

    @NotBlank(message = "대학교를 선택해주세요")
    private String university;

    @NotBlank(message = "전공을 입력해주세요")
    private String major;

    @NotNull(message = "입학년도를 입력해주세요")
    private Integer enrollmentYear;

    @NotNull(message = "졸업 유형을 선택해주세요")
    private GraduationType graduationType;

    @Valid
    private List<CompletedCourseDto> completedCourses;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompletedCourseDto {
        @NotBlank(message = "과목 코드를 입력해주세요")
        private String courseCode;

        @NotBlank(message = "과목명을 입력해주세요")
        private String courseTitle;
    }
}

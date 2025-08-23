package roarbits.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import roarbits.user.entity.GraduationType;
import java.util.List;
import jakarta.validation.constraints.NotNull;

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

    @NotNull
    @Schema(description = "졸업 유형을 선택해주세요", allowableValues = {"GENERAL", "DOUBLE_MAJOR", "MINOR"})
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

package roarbits.coursehistory.dto;

import lombok.*;
import roarbits.coursehistory.dto.CourseEnums.Semester;
import roarbits.coursehistory.dto.CourseEnums.RetakeType;
import roarbits.coursehistory.entity.CourseEntity;

@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class CourseResponse {
    private Long id;
    private Long subjectId;
    private Integer year;
    private Semester semester;
    private String courseCode;
    private String courseTitle;
    private Integer credit;
    private String category;
    private RetakeType retake;

    public static CourseResponse from(CourseEntity e) {
        return CourseResponse.builder()
                .id(e.getId())
                .subjectId(e.getSubject() != null ? e.getSubject().getId() : null)
                .year(e.getYear())
                .semester(e.getSemester())
                .courseTitle(e.getCourseTitle())
                .credit(e.getCredit())
                .category(e.getCategory())
                .retake(e.getRetake())
                .build();

    }
}

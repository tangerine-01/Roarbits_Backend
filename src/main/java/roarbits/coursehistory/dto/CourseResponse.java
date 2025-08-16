package roarbits.coursehistory.dto;

import lombok.*;
import roarbits.coursehistory.dto.CourseEnums.Semester;
import roarbits.coursehistory.dto.CourseEnums.RetakeType;

@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class CourseResponse {
    private Long id;
    private Long userId;
    private Integer year;
    private Semester semester;
    private String courseCode;
    private String courseTitle;
    private Integer credit;
    private String category;
    private RetakeType retake;
}

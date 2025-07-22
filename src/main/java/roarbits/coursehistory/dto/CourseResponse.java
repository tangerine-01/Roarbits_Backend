package roarbits.coursehistory.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {
    private Long id;
    private Long userId;
    private String courseCode;
    private String courseTitle;
    private int credit;
    private String semester;
}

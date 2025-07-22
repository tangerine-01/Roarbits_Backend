package roarbits.coursehistory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_entities")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class CourseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String courseCode;
    private String courseTitle;
    private int credit;
    private String semester;
}

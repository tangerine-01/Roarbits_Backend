package roarbits.coursehistory.entity;

import jakarta.persistence.*;
import lombok.*;
import roarbits.coursehistory.dto.CourseEnums.Semester;
import roarbits.coursehistory.dto.CourseEnums.RetakeType;


@Entity
@Table(name = "course_history",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","year","semester","course_code"}))
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CourseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private Integer year;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    @Column(nullable = false)
    private String courseCode;

    private String courseTitle;

    private Integer credit;

    @Enumerated(EnumType.STRING)
    private RetakeType retake;
}

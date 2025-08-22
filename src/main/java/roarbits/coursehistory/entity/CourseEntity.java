package roarbits.coursehistory.entity;

import jakarta.persistence.*;
import lombok.*;
import roarbits.coursehistory.dto.CourseEnums.Semester;
import roarbits.coursehistory.dto.CourseEnums.RetakeType;
import roarbits.subject.entity.Subject;


@Entity
@Table(name = "course_history",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id","year","semester","subject_id"})},
        indexes = {
        @Index(name = "idx_coursehistory_user", columnList = "user_id"),
                @Index(name = "idx_coursehistory_subject", columnList = "subject_id")
        }
)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CourseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "subject_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_coursehistory_subject")
    )
    private Subject subject;

    @Column(name = "course_title")
    private String courseTitle;

    private Integer credit;

    @Column(length = 30)
    private String category;

    @Enumerated(EnumType.STRING)
    private RetakeType retake;
}

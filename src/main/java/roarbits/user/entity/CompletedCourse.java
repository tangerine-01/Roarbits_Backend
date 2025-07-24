package roarbits.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "completed_courses")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class CompletedCourse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    private String courseCode;
    private String courseTitle;
}

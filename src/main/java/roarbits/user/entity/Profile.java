package roarbits.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "profiles")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String university;
    private String major;

    private Integer enrollmentYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "graduation_type", nullable = false)
    private GraduationType graduationType;

    @OneToMany(mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CompletedCourse> completedCourses = new ArrayList<>();
}

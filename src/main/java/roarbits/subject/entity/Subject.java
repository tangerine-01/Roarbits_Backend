package roarbits.subject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="subject_id", unique = true, nullable = false)
    private String subjectId;

    private String name;
    private String professor;
    private String classroom;
    private String description;
    private String courseType;
    private String discipline;

    @Column(name="category", length = 30)
    private String category;

    private Integer credit;
    private Integer grade;
    private Integer dayOfWeek; // (1=월, 2=화, ...)
    private Integer dayOfWeek2nd;

    private String start;
    private String end;

    private String start2nd;
    private String end2nd;
}

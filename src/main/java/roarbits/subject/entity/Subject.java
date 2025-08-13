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


    private Integer credit;
    private Integer grade;
}

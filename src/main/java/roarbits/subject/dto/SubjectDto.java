package roarbits.subject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roarbits.subject.entity.Subject;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDto {
    private Long id;
    private String name;
    private String description;
    private String courseType;
    private String discipline;
    private String category;
    private Integer dayOfWeek;
    private String start;
    private String end;

    public static SubjectDto from(Subject subject) {
        return new SubjectDto(
                subject.getId(),
                subject.getName(),
                subject.getDescription(),
                subject.getCourseType(),
                subject.getDiscipline(),
                subject.getCategory(),
                subject.getDayOfWeek(),
                subject.getStart(),
                subject.getEnd()
        );
    }
}

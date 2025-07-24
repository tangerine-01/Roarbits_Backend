package roarbits.graduation.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class GraduationRequirementRequestDto {
    private String major;
    private int totalCredits;
    private int majorCredits;
    private int electiveCredits;
    private int generalCredits;
    private boolean requiresEnglishTest;
    private String etc;
}

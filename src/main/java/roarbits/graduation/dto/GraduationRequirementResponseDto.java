package roarbits.graduation.dto;

import lombok.*;
import roarbits.graduation.entity.GraduationRequirement;

@Getter
@AllArgsConstructor
@Builder
public class GraduationRequirementResponseDto {
    private Long id;
    private String major;
    private int totalCredits;
    private int majorCredits;
    private int electiveCredits;
    private int generalCredits;
    private boolean requiresEnglishTest;
    private String etc;

    public static GraduationRequirementResponseDto fromEntity(GraduationRequirement entity) {
        return GraduationRequirementResponseDto.builder()
                .id(entity.getId())
                .major(entity.getMajor())
                .totalCredits(entity.getTotalCredits())
                .majorCredits(entity.getMajorCredits())
                .electiveCredits(entity.getElectiveCredits())
                .generalCredits(entity.getGeneralCredits())
                .requiresEnglishTest(entity.isRequiresEnglishTest())
                .etc(entity.getEtc())
                .build();
    }
}

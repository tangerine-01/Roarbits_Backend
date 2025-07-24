package roarbits.graduation.dto;

import lombok.*;

@Getter
@Builder
public class GraduationProgressDto {
    private Long studentId;
    private int totalRequiredCredits;
    private int totalEarnedCredits;
    private double progressPercentage;
}

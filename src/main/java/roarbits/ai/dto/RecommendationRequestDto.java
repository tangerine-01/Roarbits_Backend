package roarbits.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecommendationRequestDto {
    @NotBlank private String scheduleJson;
    private String purpose;
}

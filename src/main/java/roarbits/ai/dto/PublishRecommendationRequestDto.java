package roarbits.ai.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PublishRecommendationRequestDto {
    @NotNull  private Long writerId;
    @NotBlank private String title;
    @NotBlank private String content;
    @NotBlank private String postType;
}

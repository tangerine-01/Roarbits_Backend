package roarbits.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInterestRequestDto {
    @NotNull
    private Long subjectId;
}

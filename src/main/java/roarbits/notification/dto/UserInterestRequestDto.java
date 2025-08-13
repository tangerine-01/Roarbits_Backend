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
    private String interestType; // 관심 종류
    @NotNull
    private Long interestTargetId; // 관심 대상 ID
    private boolean enabled;       // 알림 활성화 여부
}

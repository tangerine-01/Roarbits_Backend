package roarbits.notification.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInterestRequestDto {
    private Long userId;           // 사용자 ID
    private String interestType;   // 관심 종류
    private Long interestTargetId; // 관심 대상 ID
    private boolean enabled;       // 알림 활성화 여부
}

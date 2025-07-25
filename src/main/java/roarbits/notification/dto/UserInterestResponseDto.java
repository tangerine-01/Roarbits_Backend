package roarbits.notification.dto;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class UserInterestResponseDto {
    private Long id;
    private Long userId;
    private String interestType;
    private Long interestTargetId;
    private boolean enabled;

    public static UserInterestResponseDto fromEntity(roarbits.notification.entity.UserInterest entity) {
        return UserInterestResponseDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .interestType(entity.getInterestType())
                .interestTargetId(entity.getInterestTargetId())
                .enabled(entity.isEnabled())
                .build();
    }
}
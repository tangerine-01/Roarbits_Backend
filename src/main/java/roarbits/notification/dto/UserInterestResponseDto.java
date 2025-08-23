package roarbits.notification.dto;

import lombok.*;
import roarbits.notification.entity.UserInterest;

@Getter
@AllArgsConstructor
@Builder
public class UserInterestResponseDto {
    private Long id;
    private Long userId;
    private Long subjectId;
    private boolean enabled;
    private String subjectName;
    private String category;

    public static UserInterestResponseDto from(UserInterest e, String subjectName, String category) {
        return UserInterestResponseDto.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .subjectId(e.getSubjectId())
                .enabled(e.isEnabled())
                .subjectName(subjectName)
                .category(category)
                .build();
    }
}
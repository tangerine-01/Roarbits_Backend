package roarbits.notification.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class UserInterestResponseDto {
    private Long id;
    private Long userId;
    private Long subjectId;

    public static UserInterestResponseDto fromEntity(roarbits.notification.entity.UserInterest entity) {
        return UserInterestResponseDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .subjectId(entity.getSubjectId())
                .build();
    }
}
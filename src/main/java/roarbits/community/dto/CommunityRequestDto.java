package roarbits.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import roarbits.community.entity.PostType;

import java.time.LocalDateTime;

public class CommunityRequestDto {
    @Getter @Setter @NoArgsConstructor
    public static class CreatePost {
        @NotBlank private String title;
        @NotBlank private String content;
        @NotNull private PostType type;

        // 위치, 시간, 모집 인원
        private Double lat;
        private Double lng;
        private LocalDateTime meetTime;
        private Integer maxParticipants;
    }

    @Getter @Setter @NoArgsConstructor
    public static class UpdatePost {
        @NotBlank private String title;
        @NotBlank private String content;
        @NotNull private PostType type;

        // 위치, 시간, 모집 인원
        private Double lat;
        private Double lng;
        private LocalDateTime meetTime;
        private Integer maxParticipants;
    }

    @Getter @Setter @NoArgsConstructor
    public static class CreateComment {
        @NotNull private Long postId;
        @NotBlank private String content;
    }

    @Getter @Setter @NoArgsConstructor
    public static class UpdateComment {
        private String content;
    }

    @Getter @Setter @NoArgsConstructor
    public static class NearByQuery {
        @NotNull private Double lat; // 위도
        @NotNull private Double lng; // 경도
        @NotNull @Positive private Double radius; // 반경 (키로미터 단위)
    }
}

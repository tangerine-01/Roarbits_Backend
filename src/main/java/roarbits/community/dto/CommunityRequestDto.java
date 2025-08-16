package roarbits.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
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
        @DecimalMin(value = "-90.0")
        @DecimalMax(value = "90.0")
        private Double lat;

        @DecimalMin(value = "-180.0")
        @DecimalMax(value = "180.0")
        private Double lng;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
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
        @Positive private Integer maxParticipants;
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

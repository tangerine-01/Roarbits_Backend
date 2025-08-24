package roarbits.community.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import roarbits.community.entity.PostType;

public class CommunityRequestDto {
    @Getter @Setter @NoArgsConstructor
    public static class CreatePost {
        @NotBlank(message = "제목은 필수입니다.") @Size(max = 100) private String title;
        @NotBlank(message = "내용은 필수입니다.") @Size(max = 5000) private String content;
        @NotNull(message = "카테고리(type)는 필수입니다.") private PostType type;
    }

    @Getter @Setter @NoArgsConstructor
    public static class UpdatePost {
        @NotBlank private String title;
        @NotBlank private String content;
        @NotNull private PostType type;
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

    /*
    @Getter @Setter @NoArgsConstructor
    public static class NearByQuery {
        @NotNull private Double lat; // 위도
        @NotNull private Double lng; // 경도
        @NotNull @Positive private Double radius; // 반경 (키로미터 단위)
    }
    */
}

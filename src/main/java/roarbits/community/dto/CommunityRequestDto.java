package roarbits.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import roarbits.community.entity.PostType;

public class CommunityRequestDto {
    @Getter @Setter @NoArgsConstructor
    public static class CreatePost {
        @NotBlank private String title;
        @NotBlank private String content;
        @NotNull private PostType type;
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
}

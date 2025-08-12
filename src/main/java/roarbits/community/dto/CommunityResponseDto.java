package roarbits.community.dto;

import lombok.Getter;
import lombok.Builder;
import roarbits.community.entity.PostType;

import java.time.LocalDateTime;

public class CommunityResponseDto {
    @Getter
    @Builder
    public static class Post {
        private Long id;
        private String title;
        private String content;
        private Long authorId;
        private long commentCount;
        private boolean deleted;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private PostType postType;

        public static Post from(roarbits.community.entity.CommunityPost p, int commentCount) {
            return Post.builder()
                    .id(p.getId())
                    .title(p.getTitle())
                    .content(p.getContent())
                    .authorId(p.getWriter().getId())
                    .commentCount(commentCount)
                    .deleted(p.getIsDeleted())
                    .createdAt(p.getCreatedAt())
                    .updatedAt(p.getUpdatedAt())
                    .postType(p.getType())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Comment {
        private Long id;
        private Long postId;
        private Long authorId;
        private String content;
        private boolean deleted;
        private LocalDateTime createdAt;

        public static Comment from(roarbits.community.entity.CommunityComment c) {
            return Comment.builder()
                    .id(c.getId())
                    .postId(c.getPost().getId())
                    .authorId(c.getWriter().getId())
                    .content(c.getContent())
                    .deleted(c.getIsDeleted())
                    .createdAt(c.getCreatedAt())
                    .build();
        }
    }
}

package roarbits.community.dto;

import lombok.*;

import java.time.LocalDateTime;

import roarbits.community.entity.CommunityComment;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String content;
    private String author;
    private LocalDateTime createdAt;

    public static CommentResponseDto from(CommunityComment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(comment.getWriter().getUsername())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}

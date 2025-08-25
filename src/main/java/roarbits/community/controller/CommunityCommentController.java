package roarbits.community.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import roarbits.community.dto.CommunityRequestDto;
import roarbits.community.dto.CommunityResponseDto;
import roarbits.community.service.CommunityService;
import roarbits.global.api.ApiResponse;
import roarbits.community.dto.CommentResponseDto;
import roarbits.community.service.CommunityCommentService;
import roarbits.global.api.SuccessCode;

import java.util.List;

@RestController
@RequestMapping("/api/community/comments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")

public class CommunityCommentController {
    private final CommunityService service;
    private final CommunityCommentService commentService;

    // 댓글 생성
    @PostMapping
    @Operation(
            summary = "댓글 생성",
            description = "게시글에 댓글을 작성합니다.",
            security = { @SecurityRequirement(name = "Authorization") })
    public ResponseEntity<CommunityResponseDto.Comment> createComment(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody CommunityRequestDto.CreateComment req) {
        return ResponseEntity.ok(service.createComment(userId, req));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    @Operation(
            summary = "댓글 수정",
            description = "작성한 댓글을 수정합니다.",
            security = { @SecurityRequirement(name = "Authorization") })
    public ResponseEntity<CommunityResponseDto.Comment> updateComment(
            @PathVariable Long commentId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody CommunityRequestDto.UpdateComment req) {
        return ResponseEntity.ok(service.updateComment(commentId, userId, req));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @Operation(
            summary = "댓글 삭제",
            description = "작성한 댓글을 삭제합니다.",
            security = { @SecurityRequirement(name = "Authorization") })
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId) {
        service.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "게시물 댓글 조회", description = "게시물 ID로 댓글 목록을 조회합니다.")
    public ApiResponse<List<CommentResponseDto>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);
        return ApiResponse.onSuccess(SuccessCode.COMMENT_LIST_SUCCESS, comments);

    }
}

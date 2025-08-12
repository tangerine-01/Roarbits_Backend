package roarbits.community.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roarbits.community.dto.CommunityRequestDto;
import roarbits.community.dto.CommunityResponseDto;
import roarbits.community.service.CommunityService;

@RestController
@RequestMapping("/api/community/comments")
@RequiredArgsConstructor
public class CommunityCommentController {
    private final CommunityService service;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<CommunityResponseDto.Comment> createComment(
            @RequestParam Long writerId,
            @Valid @RequestBody CommunityRequestDto.CreateComment req) {
        return ResponseEntity.ok(service.createComment(writerId, req));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommunityResponseDto.Comment> updateComment(
            @PathVariable Long commentId,
            @RequestParam Long writerId,
            @Valid @RequestBody CommunityRequestDto.UpdateComment req) {
        return ResponseEntity.ok(service.updateComment(commentId, writerId, req));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long writerId) {
        service.deleteComment(commentId, writerId);
        return ResponseEntity.noContent().build();
    }
}

package roarbits.community.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import roarbits.community.dto.CommunityRequestDto;
import roarbits.community.dto.CommunityResponseDto;
import roarbits.community.service.CommunityService;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")

public class CommunityPostController {
    private final CommunityService communityService;

    // 게시글 생성
    @PostMapping
    @Operation(
            summary = "게시글 생성",
            description = "커뮤니티 게시글을 작성합니다.",
            security = { @SecurityRequirement(name = "Authorization") })
    public ResponseEntity<CommunityResponseDto.Post> createPost(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody CommunityRequestDto.CreatePost req) {
        return ResponseEntity.ok(communityService.createPost(userId, req));
    }

    // 게시글 조회
    @GetMapping("/{postId}")
    @Operation(
            summary = "게시글 조회",
            description = "특정 게시글을 조회합니다.",
            security = { @SecurityRequirement(name = "Authorization") })
    public ResponseEntity<CommunityResponseDto.Post> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.getPost(postId));
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    @Operation(
            summary = "게시글 수정",
            description = "작성한 게시글을 수정합니다.",
            security = { @SecurityRequirement(name = "Authorization") })
    public ResponseEntity<CommunityResponseDto.Post> updatePost(
            @PathVariable Long postId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody CommunityRequestDto.UpdatePost req) {
        return ResponseEntity.ok(communityService.updatePost(postId, userId, req));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    @Operation(
            summary = "게시글 삭제",
            description = "작성한 게시글을 삭제합니다.",
            security = { @SecurityRequirement(name = "Authorization") })
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        communityService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    // 위치 기반 탐색
    @GetMapping("/nearby")
    @Operation(
            summary = "위치 기반 게시글 탐색",
            description = "주어진 위치와 반경 내의 게시글을 조회합니다.",
            security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<Page<CommunityResponseDto.Post>> findNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radiusMeters,
            Pageable pageable) {
        Page<CommunityResponseDto.Post> page = communityService.findNearby(lat, lng, radiusMeters, pageable);
        return ResponseEntity.ok(page);
    }
}
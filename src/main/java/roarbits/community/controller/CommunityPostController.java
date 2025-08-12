package roarbits.community.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roarbits.community.dto.CommunityRequestDto;
import roarbits.community.dto.CommunityResponseDto;
import roarbits.community.service.CommunityService;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class CommunityPostController {
    private final CommunityService service;
    private final CommunityService communityService;

    // 게시글 생성
    @PostMapping
    public ResponseEntity<CommunityResponseDto.Post> createPost(
            @RequestParam Long writerId,
            @Valid @RequestBody CommunityRequestDto.CreatePost req) {
        return ResponseEntity.ok(communityService.createPost(writerId, req));
    }

    // 게시글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<CommunityResponseDto.Post> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.getPost(postId));
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<CommunityResponseDto.Post> updatePost(
            @PathVariable Long postId,
            @RequestParam Long writerId,
            @Valid @RequestBody CommunityRequestDto.UpdatePost req) {
        return ResponseEntity.ok(communityService.updatePost(postId, writerId, req));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestParam Long writerId
    ) {
        communityService.deletePost(postId, writerId);
        return ResponseEntity.noContent().build();
    }

    // 위치 기반 탐색
    @GetMapping("/nearby")
    public ResponseEntity<Page<CommunityResponseDto.Post>> findNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radiusMeters,
            Pageable pageable) {
        Page<CommunityResponseDto.Post> page = communityService.findNearby(lat, lng, radiusMeters, pageable);
        return ResponseEntity.ok(page);
    }
}
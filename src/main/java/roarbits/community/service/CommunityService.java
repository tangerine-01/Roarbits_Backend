package roarbits.community.service;

import roarbits.community.dto.CommunityRequestDto;
import roarbits.community.dto.CommunityResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommunityService {
    //Post
    CommunityResponseDto.Post createPost(Long userId, CommunityRequestDto.CreatePost req);
    CommunityResponseDto.Post getPost(Long postId);
    CommunityResponseDto.Post updatePost(Long postId, Long userId, CommunityRequestDto.UpdatePost req);
    void deletePost(Long postId, Long userId);

    //반경 검색
    Page<CommunityResponseDto.Post> findNearby(double lat, double lng, double radiusMeters, Pageable pageable);

    //Comment
    CommunityResponseDto.Comment createComment(Long userId, CommunityRequestDto.CreateComment req);
    CommunityResponseDto.Comment updateComment(Long commentId, Long userId, CommunityRequestDto.UpdateComment req);
    void deleteComment(Long commentId, Long userId);
}

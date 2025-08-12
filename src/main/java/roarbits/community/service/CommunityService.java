package roarbits.community.service;

import roarbits.community.dto.CommunityRequestDto;
import roarbits.community.dto.CommunityResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommunityService {
    //Post
    CommunityResponseDto.Post createPost(Long writerId, CommunityRequestDto.CreatePost req);
    CommunityResponseDto.Post getPost(Long postId);
    CommunityResponseDto.Post updatePost(Long postId, Long writerId, CommunityRequestDto.UpdatePost req);
    void deletePost(Long postId, Long writerId);

    //반경 검색
    Page<CommunityResponseDto.Post> findNearby(double lat, double lng, double radiusMeters, Pageable pageable);

    //Comment
    CommunityResponseDto.Comment createComment(Long writerId, CommunityRequestDto.CreateComment req);
    CommunityResponseDto.Comment updateComment(Long commentId, Long writerId, CommunityRequestDto.UpdateComment req);
    void deleteComment(Long commentId, Long writerId);
}

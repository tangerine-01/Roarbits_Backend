package roarbits.community.service;

import roarbits.community.dto.CommunityRequestDto;
import roarbits.community.dto.CommunityResponseDto;

public interface CommunityService {
    //Post
    CommunityResponseDto.Post createPost(Long writerId, CommunityRequestDto.CreatePost req);
    CommunityResponseDto.Post getPost(Long postId);
    CommunityResponseDto.Post updatePost(Long postId, Long writerId, CommunityRequestDto.UpdatePost req);
    void deletePost(Long postId, Long writerId);

    //Comment
    CommunityResponseDto.Comment createComment(Long writerId, CommunityRequestDto.CreateComment req);
    CommunityResponseDto.Comment updateComment(Long commentId, Long writerId, CommunityRequestDto.UpdateComment req);
    void deleteComment(Long commentId, Long writerId);
}

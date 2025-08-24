package roarbits.community.service;

import org.springframework.data.domain.Page;
import roarbits.community.dto.CommunityRequestDto;
import roarbits.community.dto.CommunityResponseDto;

public interface CommunityService {
    //Post
    CommunityResponseDto.Post createPost(Long userId, CommunityRequestDto.CreatePost req);
    CommunityResponseDto.Post getPost(Long postId);
    CommunityResponseDto.Post updatePost(Long postId, Long userId, CommunityRequestDto.UpdatePost req);
    void deletePost(Long postId, Long userId);

    Page<CommunityResponseDto.Post> listPosts(Integer page, Integer size, String sort, String type);

    //Comment
    CommunityResponseDto.Comment createComment(Long userId, CommunityRequestDto.CreateComment req);
    CommunityResponseDto.Comment updateComment(Long commentId, Long userId, CommunityRequestDto.UpdateComment req);
    void deleteComment(Long commentId, Long userId);
}

package roarbits.community.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import roarbits.community.dto.CommentResponseDto;
import roarbits.community.entity.CommunityComment;
import roarbits.community.repository.CommunityCommentRepository;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {

    private final CommunityCommentRepository commentRepository;

    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        List<CommunityComment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(CommentResponseDto::from)
                .collect(Collectors.toList());
    }
}

package roarbits.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roarbits.community.dto.CommunityRequestDto;
import roarbits.community.dto.CommunityResponseDto;
import roarbits.community.entity.CommunityPost;
import roarbits.community.entity.CommunityComment;
import roarbits.community.repository.CommunityPostRepository;
import roarbits.community.repository.CommunityCommentRepository;
import roarbits.user.entity.User;
import roarbits.user.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService {

    private final CommunityPostRepository postRepo;
    private final CommunityCommentRepository commentRepo;
    private final UserRepository userRepo;

    // Post
    @Override
    @Transactional
    public CommunityResponseDto.Post createPost(Long userId, CommunityRequestDto.CreatePost req) {
        User writer = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "작성자 정보를 찾을 수 없습니다."));

        CommunityPost post = CommunityPost.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .writer(writer)
                .type(req.getType())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postRepo.save(post);
        return toPostDto(post);
    }

    @Override
    public CommunityResponseDto.Post getPost(Long postId) {
        CommunityPost post = postRepo.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "게시글을 찾을 수 없습니다."));
        return toPostDto(post);
    }

    @Override
    @Transactional
    public CommunityResponseDto.Post updatePost(Long postId, Long writerId, CommunityRequestDto.UpdatePost req) {
        CommunityPost post = postRepo.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        //작성자 확인
        if (post.getWriter() == null || !post.getWriter().getId().equals(writerId)) {
            throw new ResponseStatusException(FORBIDDEN, "작성자만 게시글을 수정할 수 있습니다.");
        }

        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setType(req.getType());
        post.setUpdatedAt(LocalDateTime.now());

        return toPostDto(post);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        var postOpt = postRepo.findByIdAndIsDeletedFalse(postId);
        if (postOpt.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }

        if (!postOpt.get().getWriter().getId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "작성자만 게시글을 삭제할 수 있습니다.");
        }

        int updated = postRepo.softDeleteByIdAndWriter_Id(postId, userId);
        if (updated == 0) {
            throw new ResponseStatusException(CONFLICT, "이미 삭제되었거나 삭제할 수 없는 게시글입니다.");
        }
    }

    // Comment
    @Override
    @Transactional
    public CommunityResponseDto.Comment createComment(Long userId, CommunityRequestDto.CreateComment req) {
        User writer = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "작성자 정보를 찾을 수 없습니다."));

        CommunityPost post = postRepo.findByIdAndIsDeletedFalse(req.getPostId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "게시글을 찾을 수 없습니다."));

        CommunityComment comment = CommunityComment.builder()
                .content(req.getContent())
                .writer(writer)
                .post(post)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        commentRepo.save(comment);
        return CommunityResponseDto.Comment.from(comment);
    }

    @Override
    @Transactional
    public CommunityResponseDto.Comment updateComment(Long commentId, Long userId, CommunityRequestDto.UpdateComment req) {
        CommunityComment comment = commentRepo.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        //작성자 확인
        if (!comment.getWriter().getId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "작성자만 댓글을 수정할 수 있습니다.");
        }

        comment.setContent(req.getContent());
        return CommunityResponseDto.Comment.from(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        int updated = commentRepo.softDeleteByIdAndWriter_Id(commentId, userId);
        if (updated == 0)
            throw new AccessDeniedException("작성자만 댓글을 삭제할 수 있습니다.");
    }

    //Helper
    private CommunityResponseDto.Post toPostDto(CommunityPost post) {
        long cnt = commentRepo.countByPostIdAndIsDeletedFalse(post.getId());
        return CommunityResponseDto.Post.from(post, Math.toIntExact(cnt));
    }
}

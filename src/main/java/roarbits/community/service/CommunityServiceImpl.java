package roarbits.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roarbits.community.dto.CommunityRequestDto;
import roarbits.community.dto.CommunityResponseDto;
import roarbits.community.entity.CommunityPost;
import roarbits.community.entity.CommunityComment;
import roarbits.community.entity.PostType;
import roarbits.community.repository.CommunityPostRepository;
import roarbits.community.repository.CommunityCommentRepository;
import roarbits.user.entity.User;
import roarbits.user.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityServiceImpl implements CommunityService {

    private final CommunityPostRepository postRepo;
    private final CommunityCommentRepository commentRepo;
    private final UserRepository userRepo;

    // Post
    @Override
    @Transactional
    public CommunityResponseDto.Post createPost(Long userId, CommunityRequestDto.CreatePost req) {
        var writer = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "작성자 정보를 찾을 수 없습니다."));

        CommunityPost post = CommunityPost.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .writer(writer)
                .type(req.getType())
                .isDeleted(false)
                .build();

        try {
            postRepo.saveAndFlush(post);
            return toPostDto(post);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ResponseStatusException(CONFLICT, "제약 조건 위반으로 저장할 수 없습니다." + rootMsg(e));
        } catch (org.springframework.http.converter.HttpMessageNotReadableException e) {
            throw new ResponseStatusException(BAD_REQUEST, "잘못된 요청입니다.");
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(getClass())
                    .error("게시글 저장 중 오류 발생: userId={}, req={}", userId, safeReq(req), e);
            throw e;
        }
    }

    private String rootMsg(Throwable t) {
        Throwable x =t;
        while (x.getCause() != null) x = x.getCause();
        return x.getMessage();
    }

    private Object safeReq(CommunityRequestDto.CreatePost req) {
        return req == null ? null : Map.of("title", req.getTitle(), "typw", String.valueOf(req.getType()));
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
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "게시글을 찾을 수 없습니다."));

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

    @Override
    public Page<CommunityResponseDto.Post> listPosts(Integer page, Integer size, String sort, String type) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 100) ? 20 : size;

        Sort sortSpec = "OLDEST".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Direction.ASC, "createdAt")
                : Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable pageable = PageRequest.of(p, s, sortSpec);

        Page<CommunityPost> pageData;
        if (type != null && !type.isBlank()) {
            PostType t;
            try {
                t = PostType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(BAD_REQUEST, "유효하지 않은 게시글 유형입니다.");
            }
            pageData = postRepo.findAllByIsDeletedFalseAndType(t,pageable);
        } else{
        pageData = postRepo.findAllByIsDeletedFalse(pageable);
    }
        return pageData.map(this::toPostDto);
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
        var c = commentRepo.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "댓글을 찾을 수 없습니다."));
        if (!c.getWriter().getId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "작성자만 댓글을 삭제할 수 있습니다.");
        }
        int updated = commentRepo.softDeleteByIdAndWriter_Id(commentId, userId);
        if (updated == 0) {
            throw new ResponseStatusException(CONFLICT, "이미 삭제되었거나 삭제할 수 없는 댓글입니다.");
        }
    }

    //Helper
    private CommunityResponseDto.Post toPostDto(CommunityPost post) {
        long cnt = commentRepo.countByPostIdAndIsDeletedFalse(post.getId());
        return CommunityResponseDto.Post.from(post, Math.toIntExact(cnt));
    }
}

package roarbits.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityServiceImpl implements CommunityService {

    private final CommunityPostRepository postRepo;
    private final CommunityCommentRepository commentRepo;
    private final UserRepository userRepo;

    // Post
    @Override
    public CommunityResponseDto.Post createPost(Long writerId, CommunityRequestDto.CreatePost req) {
        User writer = userRepo.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보를 찾을 수 없습니다."));

        CommunityPost post = CommunityPost.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .writer(writer)
                .type(req.getType())
                .lat(req.getLat())
                .lng(req.getLng())
                .meetTime(req.getMeetTime())
                .maxParticipants(req.getMaxParticipants())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postRepo.save(post);
        return toPostDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityResponseDto.Post getPost(Long postId) {
        CommunityPost post = postRepo.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return toPostDto(post);
    }

    @Override
    public CommunityResponseDto.Post updatePost(Long postId, Long writerId, CommunityRequestDto.UpdatePost req) {
        CommunityPost post = postRepo.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        //작성자 확인
        if (post.getWriter() == null || !post.getWriter().getId().equals(writerId)) {
            throw new IllegalArgumentException("작성자만 게시글을 수정할 수 있습니다.");
        }

        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setType(req.getType());
        post.setLat(req.getLat());
        post.setLng(req.getLng());
        post.setMeetTime(req.getMeetTime());
        post.setMaxParticipants(req.getMaxParticipants());

        post.setUpdatedAt(LocalDateTime.now());

        return toPostDto(post);
    }

    @Override
    public void deletePost(Long postId, Long writerId) {
        CommunityPost post = postRepo.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        //작성자 확인
        if (post.getWriter() == null || !post.getWriter().getId().equals(writerId)) {
            throw new IllegalArgumentException("작성자만 게시글을 삭제할 수 있습니다.");
        }

        post.setIsDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());
        postRepo.save(post);
    }

    // 반경 검색
    @Override
    @Transactional(readOnly = true)
    public Page<CommunityResponseDto.Post> findNearby(double lat, double lng, double radiusMeters, Pageable pageable) {
        Page<CommunityPost> page = postRepo.findNearby(lat, lng, radiusMeters, pageable);
        return page.map(p -> CommunityResponseDto.Post.from(p, (int) commentRepo.countByPostId(p.getId())));
    }

    // Comment
    @Override
    public CommunityResponseDto.Comment createComment(Long writerId, CommunityRequestDto.CreateComment req) {
        User writer = userRepo.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보를 찾을 수 없습니다."));

        CommunityPost post = postRepo.findByIdAndIsDeletedFalse(req.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

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
    public CommunityResponseDto.Comment updateComment(Long commentId, Long writerId, CommunityRequestDto.UpdateComment req) {
        CommunityComment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        //작성자 확인
        if (comment.getWriter() == null || !comment.getWriter().getId().equals(writerId)) {
            throw new IllegalArgumentException("작성자만 댓글을 수정할 수 있습니다.");
        }

        comment.setContent(req.getContent());
        return CommunityResponseDto.Comment.from(comment);
    }

    @Override
    public void deleteComment(Long commentId, Long writerId) {
        CommunityComment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        //작성자 확인
        if (comment.getWriter() == null || !comment.getWriter().getId().equals(writerId)) {
            throw new IllegalArgumentException("작성자만 댓글을 삭제할 수 있습니다.");
        }

        comment.setIsDeleted(true);
    }

    //Helper
    private CommunityResponseDto.Post toPostDto(CommunityPost post) {
        long cnt = commentRepo.countByPostId(post.getId());
        return CommunityResponseDto.Post.from(post, Math.toIntExact(cnt));
    }
}

package roarbits.community.repository;

import roarbits.community.entity.CommunityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    Page<CommunityComment> findByPostId(Long postId, Pageable pageable);
    long countByPostId(Long postId);
}

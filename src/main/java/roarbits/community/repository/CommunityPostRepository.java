package roarbits.community.repository;

import roarbits.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    Page<CommunityPost> findAllByIsDeletedFalse(Pageable pageable);
    Optional<CommunityPost> findByIdAndIsDeletedFalse(Long postId);
}

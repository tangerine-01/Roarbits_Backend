package roarbits.community.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import roarbits.community.entity.CommunityPost;
import roarbits.community.entity.PostType;

import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Page<CommunityPost> findAllByIsDeletedFalse(Pageable pageable);
    Optional<CommunityPost> findByIdAndIsDeletedFalse(Long postId);

    boolean existsByIdAndIsDeletedFalse(Long id);

    Page<CommunityPost> findAllByWriter_IdAndIsDeletedFalse(Long writerId, Pageable pageable);

    Page<CommunityPost> findAllByTypeAndIsDeletedFalse(Pageable pageable, PostType type);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE CommunityPost p
        SET p.isDeleted = true,
            p.updatedAt = CURRENT_TIMESTAMP
        WHERE p.id = :postId
            AND p.writer.id = :userId
            AND p.isDeleted = false
        """)
    int softDeleteByIdAndWriter_Id(@Param("postId") Long postId, @Param("userId") Long userId);
}
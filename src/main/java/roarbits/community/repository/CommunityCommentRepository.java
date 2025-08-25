package roarbits.community.repository;

import roarbits.community.entity.CommunityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    Page<CommunityComment> findByPostId(Long postId, Pageable pageable);
    long countByPostId(Long postId);

    Optional<CommunityComment> findByIdAndIsDeletedFalse(Long id);

    Page<CommunityComment> findByPostIdAndIsDeletedFalse(Long postId, Pageable pageable);
    long countByPostIdAndIsDeletedFalse(Long postId);

    boolean existsByIdAndWriter_Id(Long id, Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update CommunityComment c set c.isDeleted = true where c.id = :id and id(c.writer) = :userId")
    int softDeleteByIdAndWriter_Id(@Param("id") Long id, @Param("userId") Long userId);

    List<CommunityComment> findByPostId(Long postId);
}

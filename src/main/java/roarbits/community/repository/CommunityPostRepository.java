package roarbits.community.repository;

import org.springframework.data.repository.query.Param;
import roarbits.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    Page<CommunityPost> findAllByIsDeletedFalse(Pageable pageable);
    Optional<CommunityPost> findByIdAndIsDeletedFalse(Long postId);

    boolean existsByIdAndIsDeletedFalse(Long id, Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE CommunityPost p SET p.isDeleted = true WHERE p.id = :postid AND (p.writer) = :userId")
    int softDeleteByIdAndWriter_Id(@Param("postid") Long id, @Param("userId") Long userId);

    // 반경 검색(미터단위)
    @Query(value = """
    SELECT * 
    FROM community_post p 
    WHERE p.is_deleted = false 
    AND p.lat IS NOT NULL AND p.lng IS NOT NULL
    AND ST_Distance_Sphere(point(:lng, :lat), point(p.lng, p.lat)) <= :radiusMeters 
    ORDER BY ST_Distance_Sphere(point(:lng, :lat), point(p.lng, p.lat))
    """, countQuery = """
    SELECT COUNT(*) 
    FROM community_post p 
    WHERE p.is_deleted = false 
        AND p.lat IS NOT NULL AND p.lng IS NOT NULL
        AND ST_Distance_Sphere(point(:lng, :lat), point(p.lng, p.lat)) <= :radiusMeters
    """, nativeQuery = true)
   Page<CommunityPost> findNearby(@Param("lat") double lat,
                                  @Param("lng") double lng,
                                  @Param("radiusMeters") double radiusMeters,
                                  Pageable pageable);
}

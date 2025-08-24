package roarbits.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roarbits.user.entity.Profile;
import roarbits.user.entity.User;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser(User user);
    Optional<Profile> findByUser_Id(Long userId);
    Optional<Profile> findByUserId(Long userId);
    @Query("select (count(p) > 0) from Profile p " +
            "where p.user.id = :userId " +
            "and p.university is not null and p.university <> '' " +
            "and p.major      is not null and p.major      <> ''")
    boolean isStep1Done(@Param("userId") Long userId);

    @Query("select (count(p) > 0) from Profile p " +
            "where p.user.id = :userId " +
            "and p.enrollmentYear is not null")
    boolean isStep2Done(@Param("userId") Long userId);

    @Query("select (count(p) > 0) from Profile p " +
            "where p.user.id = :userId " +
            "and p.graduationType is not null")
    boolean isStep3Done(@Param("userId") Long userId);

    @Query("select (count(c) > 0) from CompletedCourse c " +
            "where c.profile.user.id = :userId")
    boolean isStep4Done(@Param("userId") Long userId);
}

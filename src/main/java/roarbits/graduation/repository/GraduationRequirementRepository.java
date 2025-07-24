package roarbits.graduation.repository;

import roarbits.graduation.entity.GraduationRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GraduationRequirementRepository extends JpaRepository<GraduationRequirement, Long> {
    List<GraduationRequirement> findByMajor(String major);
}

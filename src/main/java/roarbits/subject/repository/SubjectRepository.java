package roarbits.subject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roarbits.subject.entity.Subject;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectId(String subjectId);
}
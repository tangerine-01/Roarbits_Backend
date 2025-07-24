package roarbits.subject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roarbits.subject.entity.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectId(String subjectId);
    List<Subject> findByNameContaining(String name); // 과목명 검색
    List<Subject> findByProfessorContaining(String professor);
}
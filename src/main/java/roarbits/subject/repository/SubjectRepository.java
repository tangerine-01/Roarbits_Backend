package roarbits.subject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roarbits.subject.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
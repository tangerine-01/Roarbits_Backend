package roarbits.coursehistory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import roarbits.coursehistory.dto.CourseRequest;
import roarbits.coursehistory.dto.CourseResponse;
import roarbits.coursehistory.entity.CourseEntity;
import roarbits.coursehistory.repository.CourseRepository;
import roarbits.subject.repository.SubjectRepository;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository repo;
    private final SubjectRepository subjectRepository;

    public List<CourseResponse> getAll(Long userId) {
        return repo.findByUserId(userId)
                .stream()
                .map(CourseResponse::from)
                .toList();
    }

    @Transactional
    public CourseResponse addCourse(Long userId, CourseRequest req) {
        var subject = subjectRepository.findById(req.getSubjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 과목입니다."));

        if (repo.existsByUserIdAndYearAndSemesterAndSubject_Id(
                userId, req.getYear(), req.getSemester(), req.getSubjectId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 수강이력입니다.");
        }

        try {
            CourseEntity saved = repo.save(
                    CourseEntity.builder()
                            .userId(userId)
                            .year(req.getYear())
                            .semester(req.getSemester())
                            .subject(subject)
                            .courseTitle(req.getCourseTitle())
                            .credit(req.getCredit())
                            .category(req.getCategory())
                            .retake(req.getRetake())
                            .build()
            );

            return CourseResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 수강이력입니다.");
        }
    }

    @Transactional
    public void delete(Long userId, Long courseId) {
        CourseEntity e = repo.findByIdAndUserId(courseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 수강이력입니다."));
        try {
            repo.delete(e);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "참조 중이라 삭제할 수 없습니다.");
        }
    }
}

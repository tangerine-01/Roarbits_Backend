package roarbits.coursehistory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roarbits.coursehistory.dto.CourseRequest;
import roarbits.coursehistory.dto.CourseResponse;
import roarbits.coursehistory.entity.CourseEntity;
import roarbits.coursehistory.repository.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository repo;

    public List<CourseResponse> getAll(Long userId) {
        return repo.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CourseResponse addCourse(Long userId, CourseRequest req) {
        // 중복 체크
        if (repo.existsByUserIdAndYearAndSemesterAndCourseCode(
                userId, req.getYear(), req.getSemester(), req.getCourseCode())) {
            throw new IllegalArgumentException("이미 등록된 수강이력입니다.");
        }

        // 저장
        CourseEntity saved = repo.save(
                CourseEntity.builder()
                        .userId(userId)
                        .year(req.getYear())
                        .semester(req.getSemester())
                        .courseCode(req.getCourseCode())
                        .courseTitle(req.getCourseTitle()) // subject 마스터 쓰면 여기서 오버라이드
                        .credit(req.getCredit())
                        .retake(req.getRetake())
                        .build()
        );

        return toDto(saved);
    }

    @Transactional
    public void delete(Long userId, Long courseId) {
        CourseEntity e = repo.findByIdAndUserId(courseId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수강이력입니다."));
        repo.delete(e);
    }

    private CourseResponse toDto(CourseEntity e) {
        return CourseResponse.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .year(e.getYear())
                .semester(e.getSemester())
                .courseCode(e.getCourseCode())
                .courseTitle(e.getCourseTitle())
                .credit(e.getCredit())
                .retake(e.getRetake())
                .build();
    }
}

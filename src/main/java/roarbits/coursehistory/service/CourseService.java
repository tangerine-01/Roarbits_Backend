package roarbits.coursehistory.service;

import roarbits.coursehistory.dto.CourseResponse;
import roarbits.coursehistory.entity.CourseEntity;
import roarbits.coursehistory.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {
    private final CourseRepository repo;

    @Transactional
    public List<CourseResponse> getAll(Long userId) {
        return repo.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private CourseResponse toDto(CourseEntity e) {
        return CourseResponse.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .courseCode(e.getCourseCode())
                .courseTitle(e.getCourseTitle())
                .credit(e.getCredit())
                .semester(e.getSemester())
                .build();
    }
}

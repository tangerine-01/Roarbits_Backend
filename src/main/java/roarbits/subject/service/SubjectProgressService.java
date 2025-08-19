package roarbits.subject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import roarbits.subject.dto.SubjectProgressResponseDto;
import roarbits.coursehistory.repository.CourseRepository;
import roarbits.coursehistory.repository.CourseRepository.EarnedByCategory;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class SubjectProgressService {
    public final CourseRepository courseRepository;
    public SubjectProgressResponseDto getMySubjectProgress(Long userId) {
        List<EarnedByCategory> rows = courseRepository.sumEarnedByCategory(userId);
        List<SubjectProgressResponseDto.CategoryItem> items = rows.stream()
                .map(r -> {
                    long earned = r.getEarned() == null ? 0L : r.getEarned();
                    String category = r.getCategory() == null ? "UNKNOWN" : r.getCategory();
                    return SubjectProgressResponseDto.CategoryItem.builder()
                            .category(category)
                            .credits(earned)
                            .build();
                })
                .toList();
        long total = 0L;
        for (var i : items) total += i.getCredits();
        return SubjectProgressResponseDto.builder()
                .userId(userId)
                .totalCredits(total)
                .categories(items)
                .build();
    }
}

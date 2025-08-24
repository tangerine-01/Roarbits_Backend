package roarbits.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.server.ResponseStatusException;
import roarbits.notification.dto.UserInterestResponseDto;
import roarbits.notification.entity.UserInterest;
import roarbits.notification.repository.UserInterestRepository;
import roarbits.subject.entity.Subject;
import roarbits.subject.repository.SubjectRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInterestService {
    private final SubjectRepository subjectRepository;
    private final UserInterestRepository repository;

    @Transactional
    public UserInterestResponseDto upsertSubjectInterest(Long userId, Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "과목을 찾을 수 없습니다."));

        String interestType = normalizeCategory(subject.getCategory(), subject);

        UserInterest interest = repository.findByUserIdAndSubjectId(userId, subjectId)
                .orElseGet(() -> repository.save(
                        UserInterest.builder()
                                .userId(userId)
                                .subjectId(subjectId)
                                .interestType(interestType)
                                .enabled(true)
                                .createdAt(LocalDateTime.now())
                                .build()
                ));

        return UserInterestResponseDto.from(interest, subject.getName(), interestType);
    }

    // 관심 알림 설정 등록 또는 수정
    @Transactional
    public void saveOrUpdateInterest(Long userId, Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "과목 없음");
        }
        if (repository.existsByUserIdAndSubjectId(userId, subjectId)) return;
        try {
            repository.saveAndFlush(UserInterest.builder()
                    .userId(userId)
                    .subjectId(subjectId)
                    .interestType("SUBJECT")
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .build());
        } catch (DataIntegrityViolationException e) {
            String root = org.springframework.core.NestedExceptionUtils.getMostSpecificCause(e).getMessage();
            if (root != null && root.toLowerCase().contains("duplicate")) return;
            throw e;
        }
    }

    @Transactional
    public void deleteInterest(Long userId, Long subjectId) {
        long n = repository.deleteByUserIdAndSubjectId(userId, subjectId);
        if (n == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "즐겨찾기 없음");
    }

    public boolean isInterest(Long userId, Long subjectId) {
        return repository.existsByUserIdAndSubjectId(userId, subjectId);
    }

    public List<Long> myInterestSubjectIds(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(UserInterest::getSubjectId).toList();
    }

    private String normalizeCategory(String category, Subject s) {
        String c = (category == null || category.isBlank()) ? deriveCategory(s) : category;
        return c.toUpperCase();
    }

    private String deriveCategory(Subject s) {
        String d = (s.getDiscipline() == null ? "" : s.getDiscipline()).toUpperCase();
        if (d.contains("전공") || d.contains("MAJOR")) return "MAJOR";
        if (d.contains("교양") || d.contains("LIBERAL") || d.contains("GE")) return "LIBERAL";
        return "GENERAL";
    }
}
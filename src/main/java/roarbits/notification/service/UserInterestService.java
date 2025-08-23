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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserInterestService {
    private final SubjectRepository subjectRepository;
    private final UserInterestRepository repository;

    public UserInterestResponseDto upsertSubjectInterest(Long userId, Long subjectId) {
        var subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "과목을 찾을 수 없습니다."));

        String category = subject.getCategory();
        if (category == null || category.isBlank()) {
            category = deriveCategory(subject);
            subject.setCategory(category);
        }

        var interest = repository.findByUserIdAndSubjectId(userId, subjectId)
                .orElseGet(() -> new UserInterest(userId, subjectId));

        interest.setEnabled(true);
        interest.setCategory(category);

        var saved = repository.save(interest);
        return UserInterestResponseDto.from(saved, subject.getName(), category);
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
                    .build());
        } catch (DataIntegrityViolationException e) {
        }
    }

    // 관심 알림 삭제
    @Transactional
    public void deleteInterest(Long userId, Long subjectId) {
        long n = repository.deleteByUserIdAndSubjectId(userId, subjectId);
        if (n == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "즐겨찾기 없음");
    }

    // 즐겨찾기 여부 확인
    public boolean isInterest(Long userId, Long subjectId) {
        return repository.existsByUserIdAndSubjectId(userId, subjectId);
    }

    public List<Long> myInterestSubjectIds(Long userId) {
        return repository.findAllByUserId(userId).stream().map(UserInterest::getSubjectId).toList();
    }

    private String deriveCategory(Subject s) {
        String d = (s.getDiscipline() == null ? "" : s.getDiscipline()).toUpperCase();
        if (d.contains("전공") || d.contains("MAJOR")) return "MAJOR";
        if (d.contains("교양") || d.contains("LIBERAL") || d.contains("GE")) return "LIBERAL";
        return "GENERAL";
    }
}
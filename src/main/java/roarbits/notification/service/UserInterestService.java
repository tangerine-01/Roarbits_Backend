package roarbits.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roarbits.notification.entity.UserInterest;
import roarbits.notification.repository.UserInterestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInterestService {

    private final UserInterestRepository repository;

    // 관심 알림 설정 등록 또는 수정
    @Transactional
    public void saveOrUpdateInterest(Long userId, Long subjectId) {
        if(repository.existsByUserIdAndSubjectId(userId, subjectId)) return;
        repository.save(UserInterest.builder().userId(userId).subjectId(subjectId).build());
    }

    // 관심 알림 삭제
    @Transactional
    public void deleteInterest(Long userId, Long subjectId) {
        repository.deleteByUserIdAndSubjectId(userId, subjectId);
    }

    // 즐겨찾기 여부 확인
    public boolean isInterest(Long userId, Long subjectId) {
        return repository.existsByUserIdAndSubjectId(userId, subjectId);
    }

    public List<Long> myInterestSubjectIds(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(UserInterest::getSubjectId)
                .toList();
    }
}
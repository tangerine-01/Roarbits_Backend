package roarbits.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roarbits.notification.dto.UserInterestRequestDto;
import roarbits.notification.dto.UserInterestResponseDto;
import roarbits.notification.entity.UserInterest;
import roarbits.notification.repository.UserInterestRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserInterestService {

    private final UserInterestRepository repository;

    // 관심 알림 설정 등록 또는 수정
    public UserInterestResponseDto saveOrUpdateInterest(UserInterestRequestDto dto) {
        UserInterest entity = repository.findByUserIdAndInterestTypeAndInterestTargetId(
                        dto.getUserId(), dto.getInterestType(), dto.getInterestTargetId())
                .orElse(UserInterest.builder()
                        .userId(dto.getUserId())
                        .interestType(dto.getInterestType())
                        .interestTargetId(dto.getInterestTargetId())
                        .build());

        entity.setEnabled(dto.isEnabled());
        return UserInterestResponseDto.fromEntity(repository.save(entity));
    }

    // 사용자 관심 목록 조회
    @Transactional(readOnly = true)
    public List<UserInterestResponseDto> getUserInterests(Long userId) {
        List<UserInterest> list = repository.findByUserId(userId);
        return list.stream()
                .map(UserInterestResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 관심 알림 삭제
    public void deleteInterest(Long id) {
        repository.deleteById(id);
    }
}
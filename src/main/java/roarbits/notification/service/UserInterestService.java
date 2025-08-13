package roarbits.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    public UserInterestResponseDto saveOrUpdateInterest(Long userId, UserInterestRequestDto dto) {
        UserInterest entity = repository.findByUserIdAndInterestTypeAndInterestTargetId(
                        userId, dto.getInterestType(), dto.getInterestTargetId())
                .orElseGet(() -> UserInterest.builder()
                        .userId(userId)
                        .interestType(dto.getInterestType())
                        .interestTargetId(dto.getInterestTargetId())
                        .build());

        entity.setEnabled(dto.isEnabled());
        return UserInterestResponseDto.fromEntity(repository.save(entity));
    }

    // 사용자 관심 목록 조회
    @Transactional(readOnly = true)
    public List<UserInterestResponseDto> getUserInterests(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(UserInterestResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 관심 알림 삭제
    public void deleteInterest(Long userId, Long id) {
        UserInterest e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "즐겨찾기가 존재하지 않습니다."));
        if (!e.getUserId().equals(userId)) {
            throw new AccessDeniedException("해당 즐겨찾기를 삭제할 권한이 없습니다.");
        }
        repository.delete(e);
    }
}
package roarbits.graduation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import roarbits.graduation.dto.*;
import roarbits.graduation.entity.GraduationRequirement;
import roarbits.graduation.repository.GraduationRequirementRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GraduationRequirementService {
    private final GraduationRequirementRepository repository;

    // 전체 조회
    public List<GraduationRequirementResponseDto> getAllRequirements() {
        return repository.findAll().stream()
                .map(GraduationRequirementResponseDto::fromEntity)
                .toList();
    }

    // 전공별 조회
    public List<GraduationRequirementResponseDto> getRequirementsByMajor(String major) {
        return repository.findByMajor(major).stream()
                .map(GraduationRequirementResponseDto::fromEntity)
                .toList();
    }

    // 졸업 진행도 조회 (사용자용)
    public GraduationProgressDto getGraduationProgressForUser(Long userId) {
        return getGraduationProgress(userId);
    }

    // 등록
    @Transactional
    public GraduationRequirementResponseDto createRequirement(GraduationRequirementRequestDto dto) {
        GraduationRequirement entity = GraduationRequirement.builder()
                .major(dto.getMajor())
                .totalCredits(dto.getTotalCredits())
                .majorCredits(dto.getMajorCredits())
                .electiveCredits(dto.getElectiveCredits())
                .generalCredits(dto.getGeneralCredits())
                .requiresEnglishTest(dto.isRequiresEnglishTest())
                .etc(dto.getEtc())
                .build();
        return GraduationRequirementResponseDto.fromEntity(repository.save(entity));
    }

    // 삭제
    @Transactional
    public void deleteRequirement(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("해당 졸업요건이 존재하지 않습니다.");
        }
        repository.deleteById(id);
    }

    // 수정
    @Transactional
    public GraduationRequirementResponseDto updateRequirement(Long id, GraduationRequirementRequestDto dto) {
        GraduationRequirement requirement = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 졸업요건이 존재하지 않습니다."));

        requirement.update(
                dto.getMajor(),
                dto.getTotalCredits(),
                dto.getMajorCredits(),
                dto.getElectiveCredits(),
                dto.getGeneralCredits(),
                dto.isRequiresEnglishTest(),
                dto.getEtc()
        );
        return GraduationRequirementResponseDto.fromEntity(requirement);
    }

    public GraduationProgressDto getGraduationProgress(Long userId) {
        // TODO: 실제 로직 작성 필요
        // 지금은 임시 응답 객체 반환
        return GraduationProgressDto.builder()
                .userId(userId)
                .totalEarnedCredits(90)
                .totalRequiredCredits(130)
                .progressPercentage(69.2)
                .build();
    }
}

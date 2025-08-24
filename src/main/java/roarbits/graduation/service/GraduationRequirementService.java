package roarbits.graduation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import roarbits.coursehistory.repository.CourseRepository;
import roarbits.graduation.dto.GraduationProgressDto;
import roarbits.graduation.dto.GraduationRequirementRequestDto;
import roarbits.graduation.dto.GraduationRequirementResponseDto;
import roarbits.graduation.entity.GraduationRequirement;
import roarbits.graduation.repository.GraduationRequirementRepository;
import roarbits.user.entity.User;
import roarbits.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GraduationRequirementService {
    private final GraduationRequirementRepository repository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        String major = user.getProfile().getMajor();

        GraduationRequirement requirement = repository.findByMajor(major).stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 전공의 졸업요건이 존재하지 않습니다."));

        var histories = courseRepository.findByUserId(userId);
        int earnedTotalCredits = histories.stream().mapToInt(h -> h.getSubject().getCredit()).sum();
        int earnedMajorCredits = histories.stream()
                .filter(h -> "MAJOR".equals(h.getCategory()))
                .mapToInt(h -> h.getSubject().getCredit()).sum();
        int earnedElectiveCredits = histories.stream()
                .filter(h -> "ELECTIVE".equals(h.getCategory()) || "LIBERAL".equals(h.getCategory()))
                .mapToInt(h -> h.getSubject().getCredit()).sum();
        int earnedGeneralCredits = histories.stream()
                .filter(h -> "GENERAL".equals(h.getCategory()))
                .mapToInt(h -> h.getSubject().getCredit()).sum();

        int reqTotalCredits = requirement.getTotalCredits();
        int reqMajorCredits = requirement.getMajorCredits();
        int reqElectiveCredits = requirement.getElectiveCredits();
        int reqGeneralCredits = requirement.getGeneralCredits();

        double pct = reqTotalCredits > 0 ? Math.min(100.0, (earnedTotalCredits * 100.0) / reqTotalCredits) : 0.0;

        return GraduationProgressDto.builder()
                .userId(userId)
                .totalEarnedCredits(earnedTotalCredits)
                .totalRequiredCredits(reqTotalCredits)
                .progressPercentage(round1(pct))
                .build();
    }

    private double round1(double pct) {
        return Math.round(pct * 10) / 10.0;
    }
}

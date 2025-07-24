package roarbits.graduation.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;

import roarbits.graduation.service.GraduationRequirementService;
import roarbits.graduation.dto.GraduationRequirementRequestDto;
import roarbits.graduation.dto.GraduationRequirementResponseDto;
import roarbits.graduation.dto.GraduationProgressDto;

@RestController
@RequestMapping("/api/graduation")
@RequiredArgsConstructor
public class GraduationRequirementController {

    private final GraduationRequirementService graduationRequirementService;

    // 전체 졸업요건 조회
    @GetMapping("/requirements")
    public ResponseEntity<List<GraduationRequirementResponseDto>> getAllRequirements() {
        List<GraduationRequirementResponseDto> requirements = graduationRequirementService.getAllRequirements();
        return ResponseEntity.ok(requirements);
    }

    // 졸업요건 등록
    @PostMapping("/requirements")
    public ResponseEntity<GraduationRequirementResponseDto> createRequirement(
            @RequestBody GraduationRequirementRequestDto requestDto) {
        GraduationRequirementResponseDto created = graduationRequirementService.createRequirement(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 졸업요건 수정
    @PutMapping("/requirements/{id}")
    public ResponseEntity<GraduationRequirementResponseDto> updateRequirement(
            @PathVariable Long id,
            @RequestBody GraduationRequirementRequestDto requestDto) {
        GraduationRequirementResponseDto updated = graduationRequirementService.updateRequirement(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    // 졸업요건 삭제
    @DeleteMapping("/requirements/{id}")
    public ResponseEntity<Void> deleteRequirement(@PathVariable Long id) {
        graduationRequirementService.deleteRequirement(id);
        return ResponseEntity.noContent().build();
    }

    // 졸업 진행도 조회 (예: 학생 ID로)
    @GetMapping("/progress/{studentId}")
    public ResponseEntity<GraduationProgressDto> getGraduationProgress(@PathVariable Long studentId) {
        GraduationProgressDto progress = graduationRequirementService.getGraduationProgress(studentId);
        return ResponseEntity.ok(progress);
    }
}

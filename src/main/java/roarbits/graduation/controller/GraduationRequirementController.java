package roarbits.graduation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import java.util.List;

import roarbits.graduation.service.GraduationRequirementService;
import roarbits.graduation.dto.GraduationRequirementRequestDto;
import roarbits.graduation.dto.GraduationRequirementResponseDto;
import roarbits.graduation.dto.GraduationProgressDto;

@Tag(name = "Graduation", description = "졸업 요건 관리 API")
@RestController
@RequestMapping("/api/graduation")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
public class GraduationRequirementController {

    private final GraduationRequirementService graduationRequirementService;

    // 전체 졸업요건 조회
    @GetMapping("/requirements")
    @Operation(
            summary = "졸업요건 전체 조회",
            description = "모든 졸업 요건을 조회합니다.",
            security = {@SecurityRequirement(name = "Authorization")})
    public ResponseEntity<List<GraduationRequirementResponseDto>> getAllRequirementsForUser() {
        return ResponseEntity.ok(graduationRequirementService.getAllRequirements());
    }

    // 졸업 진행도 조회 (예: 학생 ID로)(사용자용)
    @GetMapping("/progress/me")
    @Operation(
            summary = "내 졸업 진행도 조회",
            description = "현재 로그인된 사용자의 졸업 진행도를 조회합니다.",
            security = {@SecurityRequirement(name = "Authorization")})
    public ResponseEntity<GraduationProgressDto> getGraduationProgress(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId) {
        GraduationProgressDto progress = graduationRequirementService.getGraduationProgressForUser(userId);
        return ResponseEntity.ok(progress);
    }

    // 졸업요건 조회(관리자용)
    @GetMapping("/admin/requirements")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "관리자용 졸업요건 조회",
            description = "관리자가 모든 졸업 요건을 조회합니다.",
            security = {@SecurityRequirement(name = "Authorization")})
    public ResponseEntity<List<GraduationRequirementResponseDto>> getAllRequirementsForAdmin() {
        return ResponseEntity.ok(graduationRequirementService.getAllRequirements());
    }

    // 졸업요건 등록(관리자용)
    @PostMapping("/admin/requirements")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "졸업요건 등록",
            description = "관리자가 졸업 요건을 등록합니다.",
            security = {@SecurityRequirement(name = "Authorization")})
    public ResponseEntity<GraduationRequirementResponseDto> createRequirement(
            @Valid @RequestBody GraduationRequirementRequestDto requestDto) {
        var created = graduationRequirementService.createRequirement(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 졸업요건 수정(관리자용)
    @PutMapping("/admin/requirements/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "졸업요건 수정",
            description = "관리자가 졸업 요건을 수정합니다.",
            security = {@SecurityRequirement(name = "Authorization")})
    public ResponseEntity<GraduationRequirementResponseDto> updateRequirement(
            @PathVariable Long id,
            @Valid @RequestBody GraduationRequirementRequestDto requestDto) {
        var updated = graduationRequirementService.updateRequirement(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    // 졸업요건 삭제(관리자용)
    @DeleteMapping("/admin/requirements/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "졸업요건 삭제",
            description = "관리자가 졸업 요건을 삭제합니다.",
            security = {@SecurityRequirement(name = "Authorization")})
    public ResponseEntity<Void> deleteRequirement(@PathVariable Long id) {
        graduationRequirementService.deleteRequirement(id);
        return ResponseEntity.noContent().build();
    }

    // 임의 학생 졸업 진행도 조회 (관리자용)
    @GetMapping("/admin/progress/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "관리자용 졸업 진행도 조회",
            description = "관리자가 특정 학생의 졸업 진행도를 조회합니다.",
            security = {@SecurityRequirement(name = "Authorization")})
    public ResponseEntity<GraduationProgressDto> getGraduationProgressByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(graduationRequirementService.getGraduationProgress(userId));
    }
}

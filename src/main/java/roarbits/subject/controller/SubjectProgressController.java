package roarbits.subject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import roarbits.subject.dto.SubjectProgressResponseDto;
import roarbits.subject.service.SubjectProgressService;

@Tag(name = "Subject Progress", description = "과목 진척도 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subject-progress")
@SecurityRequirement(name = "bearerAuth")

public class SubjectProgressController {
    private final SubjectProgressService subjectProgressService;
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내 과목 진척도 조회", description = "학점")
    public ResponseEntity<SubjectProgressResponseDto> getMySubjectProgress(
            @AuthenticationPrincipal (expression = "id") Long userId)
    { return ResponseEntity.ok(subjectProgressService.getMySubjectProgress(userId)); }
}

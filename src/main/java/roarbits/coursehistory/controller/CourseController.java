package roarbits.coursehistory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import roarbits.coursehistory.dto.CourseRequest;
import roarbits.coursehistory.dto.CourseResponse;
import roarbits.coursehistory.service.CourseService;
import roarbits.login.auth.CustomUserDetails;

import java.util.List;

@Tag(name = "Course History", description = "수강 이력 관리 API")
@RestController
@RequestMapping("/api/course-histories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")

public class CourseController {

    private final CourseService service;

    // 생성
    @PostMapping("")
    @Operation(
            summary = "수강 이력 등록",
            description = "현재 로그인된 사용자의 수강 이력을 등록합니다.",
            security = { @SecurityRequirement(name = "Authorization") }
    )
    public ResponseEntity<CourseResponse> addCourse(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails me,
            @RequestBody(description = "등록할 수강 이력 정보") @Valid
            @org.springframework.web.bind.annotation.RequestBody CourseRequest req) {
        var created = service.addCourse(me.getId(), req);
        return ResponseEntity.ok(created);
    }

    // 목록 조회
    @GetMapping
    @Operation(
            summary = "수강 이력 목록 조회",
            description = "현재 로그인된 사용자의 모든 수강 이력을 조회합니다.",
            security = { @SecurityRequirement(name = "Authorization") })
    public ResponseEntity<List<CourseResponse>> list(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails me) {
        return ResponseEntity.ok(service.getAll(me.getId()));
    }

    // 삭제
    @DeleteMapping("/{id}")
    @Operation(
            summary = "수강 이력 삭제",
            description = "현재 로그인된 사용자의 특정 수강 이력을 삭제합니다.",
            security = { @SecurityRequirement(name = "Authorization") })
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails me,
            @Parameter(description = "삭제할 수강 이력 ID")
            @PathVariable Long id) {
        service.delete(me.getId(), id);
        return ResponseEntity.noContent().build();
    }
}

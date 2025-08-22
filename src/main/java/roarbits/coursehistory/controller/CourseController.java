package roarbits.coursehistory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.server.ResponseStatusException;
import roarbits.coursehistory.dto.CourseRequest;
import roarbits.coursehistory.dto.CourseResponse;
import roarbits.coursehistory.service.CourseService;
import roarbits.login.auth.CustomUserDetails;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;

@Tag(name = "Course History", description = "수강 이력 관리 API")
@RestController
@RequestMapping("/api/course-histories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")

public class CourseController {

    private final CourseService service;
    private record IdsRequest(List<Long> ids) {}

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
            @RequestBody(description = "등록할 수강 이력 정보")
            @Valid
            @org.springframework.web.bind.annotation.RequestBody CourseRequest req
        ) {
            if (me == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
            CourseResponse saved = service.addCourse(me.getId(), req);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
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
            description = "현재 로그인된 사용자의 특정 수강 이력을 삭제합니다.")
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails me,
            @Parameter(description = "삭제할 수강 이력 ID")
            @PathVariable Long id)
    {
        if (me == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
        try {
            service.delete(me.getId(), id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "참조 중이라 삭제할 수 없습니다.");
        }
    }

}

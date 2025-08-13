package roarbits.coursehistory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import roarbits.coursehistory.dto.CourseRequest;
import roarbits.coursehistory.dto.CourseResponse;
import roarbits.coursehistory.service.CourseService;
import roarbits.login.auth.CustomUserDetails;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/course-histories")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;

    // 생성
    @PostMapping("")
    @Operation(security = { @SecurityRequirement(name = "Authorization") })
    public ResponseEntity<CourseResponse> addCourse(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails me,
            @Valid @RequestBody CourseRequest req) {
        return ResponseEntity.ok(service.addCourse(me.getId(), req));
    }

    // 목록 조회
    @GetMapping
    @Operation(security = { @SecurityRequirement(name = "Authorization") })
    public ResponseEntity<List<CourseResponse>> list(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails me) {
        return ResponseEntity.ok(service.getAll(me.getId()));
    }

    // 삭제
    @DeleteMapping("/{id}")
    @Operation(security = { @SecurityRequirement(name = "Authorization") })
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails me,
            @PathVariable Long id) {
        service.delete(me.getId(), id);
        return ResponseEntity.noContent().build();
    }
}

package roarbits.coursehistory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import roarbits.coursehistory.dto.CourseRequest;
import roarbits.coursehistory.dto.CourseResponse;
import roarbits.coursehistory.service.CourseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import roarbits.login.auth.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/course-histories")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;

    @PostMapping("/api/course-histories")
    public ResponseEntity<CourseResponse> addCourse(
            @AuthenticationPrincipal CustomUserDetails me,
            @Valid @RequestBody CourseRequest req) {
        return ResponseEntity.ok(service.addCourse(me.getId(), req));
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> list(
            @AuthenticationPrincipal CustomUserDetails me) {
        return ResponseEntity.ok(service.getAll(me.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails me,
            @PathVariable Long id) {
        service.delete(id, me.getId());
        return ResponseEntity.noContent().build();
    }
}

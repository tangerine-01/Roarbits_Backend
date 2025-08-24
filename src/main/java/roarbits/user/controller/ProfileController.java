package roarbits.user.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;
import roarbits.onboarding.dto.StepFlags;
import roarbits.user.entity.CompletedCourse;
import roarbits.user.dto.ProfileDto;
import roarbits.user.service.ProfileService;
import roarbits.login.auth.CustomUserDetails;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import roarbits.onboarding.service.OnboardingService;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "프로필 단계별 저장/수정 API")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class ProfileController {
    private final ProfileService profileService;
    private final OnboardingService onboardingService;


    @PostMapping(
            value = "/step1",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "학교/전공")
    public ResponseEntity<Map<String, Object>> step1(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails me,
            @Valid @RequestBody ProfileDto dto
    ) {
        if (me == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
        Long userId = me.getId();
        profileService.saveStep1(userId, dto.getUniversity(), dto.getMajor());
        StepFlags steps = onboardingService.refreshAndGetFlags(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", "ok", "step", 1, "message", "profile step1 saved"));
    }

    @PostMapping(
            value = "/step2",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "입학년도")
    public ResponseEntity<Map<String, Object>> step2(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails me,
            @Valid @RequestBody ProfileDto dto
    ) {
        if (me == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
        Long userId = me.getId();
        profileService.saveStep2(userId, dto.getEnrollmentYear());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", "ok", "step",2, "message", "profile step2 saved"));
    }

    @PostMapping(
            value = "/step3",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "졸업유형")
    public ResponseEntity<Map<String, Object>> step3(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails me,
            @Valid @RequestBody ProfileDto dto
    ) {
        if (me == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
        Long userId = me.getId();
        profileService.saveStep3(userId, dto.getGraduationType());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", "ok", "step",3, "message", "profile step3 saved"));
    }

    public static class Step4Request {
        private List<CourseItem> completedCourses;
        public List<CourseItem> getCompletedCourses() { return completedCourses; }
        public void setCompletedCourses(List<CourseItem> completedCourses) {this.completedCourses = completedCourses; }
    }

    public static class CourseItem {
        private String courseCode;
        private String courseTitle;
        public String getCourseCode() { return courseCode; }
        public String getCourseTitle() { return courseTitle; }
        public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    }


    @PostMapping(
            value = "/step4",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "이수 과목")
    public ResponseEntity<Map<String, Object>> step4(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails me,
            @RequestBody Step4Request req
    ) {
        if (me == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
        final Long userId = me.getId();

        List<CompletedCourse> courses =
                (req != null && req.getCompletedCourses() != null ? req.getCompletedCourses() : List.<CourseItem>of())
                .stream()
                .map(d -> CompletedCourse.builder()
                        .courseTitle(d.getCourseTitle())
                        .build())
                .collect(Collectors.toList());

        profileService.saveStep4(me.getId(), courses);
        StepFlags steps = onboardingService.refreshAndGetFlags(userId);

        boolean done = profileService.isProfileCompleted(me.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status","ok","step",4,"count", courses.size(),
                        "isCompleted", steps.isCompleted(),
                        "steps", steps, "message","profile step4 saved"));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal CustomUserDetails me,
            @Valid
            @RequestBody ProfileDto dto
    ) {
        if (me == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
        profileService.updateProfile(me.getId(), dto);
        return ResponseEntity.noContent().build();
    }
}

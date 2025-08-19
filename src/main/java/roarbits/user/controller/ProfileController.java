package roarbits.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import roarbits.user.entity.CompletedCourse;
import roarbits.user.dto.ProfileDto;
import roarbits.user.service.ProfileService;
import roarbits.user.service.UserService;
import roarbits.user.entity.User;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "프로필 단계별 저장/수정 API")
public class ProfileController {
    private final ProfileService profileService;
    private final UserService    userService;

    @PostMapping(
            value = "/step1",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "학교/전공")
    public ResponseEntity<Map<String, Object>> step1(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProfileDto dto
    ) {
        Long userId = user.getId();
        profileService.saveStep1(userId, dto.getUniversity(), dto.getMajor());
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
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProfileDto dto
    ) {
        Long userId = user.getId();
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
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProfileDto dto
    ) {
        Long userId = user.getId();
        profileService.saveStep3(userId, dto.getGraduationType());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", "ok", "step",3, "message", "profile step3 saved"));
    }

    @PostMapping(
            value = "/step4",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "이수 과목")
    public ResponseEntity<Map<String, Object>> step4(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProfileDto dto
    ) {
        Long userId = user.getId();

        List<CompletedCourse> courses = dto.getCompletedCourses().stream()
                .map(d -> CompletedCourse.builder()
                        .courseCode(d.getCourseCode())
                        .courseTitle(d.getCourseTitle())
                        .build())
                .collect(Collectors.toList());

        profileService.saveStep4(userId, courses);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status","ok","step",4,"count", courses.size(), "message","profile step4 saved"));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid
            @RequestBody ProfileDto dto
    ) {
        Long userId = user.getId();
        profileService.updateProfile(userId, dto);
        return ResponseEntity.noContent().build();
    }
}

package roarbits.user.controller;

import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final UserService    userService;

    @PostMapping("/step1")
    public ResponseEntity<Void> step1(
            @RequestParam Long userId,
            @Valid @RequestBody ProfileDto dto
    ) {
        profileService.saveStep1(userId, dto.getUniversity(), dto.getMajor());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/step2")
    public ResponseEntity<Void> step2(
            @RequestParam Long userId,
            @Valid @RequestBody ProfileDto dto
    ) {
        profileService.saveStep2(userId, dto.getEnrollmentYear());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/step3")
    public ResponseEntity<Void> step3(
            @RequestParam Long userId,
            @Valid @RequestBody ProfileDto dto
    ) {
        profileService.saveStep3(userId, dto.getGraduationType());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/step4")
    public ResponseEntity<Void> step4(
            @RequestParam Long userId,
            @Valid
            @RequestBody ProfileDto dto
    ) {
        List<CompletedCourse> courses = dto.getCompletedCourses().stream()
                .map(d -> CompletedCourse.builder()
                        .courseCode(d.getCourseCode())
                        .courseTitle(d.getCourseTitle())
                        .build())
                .collect(Collectors.toList());

        profileService.saveStep4(userId, courses);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid
            @RequestBody ProfileDto dto
    ) {
        Long userId = user.getUserId();
        profileService.updateProfile(userId, dto);
        return ResponseEntity.noContent().build();
    }
}

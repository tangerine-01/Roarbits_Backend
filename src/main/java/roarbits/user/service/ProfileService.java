package roarbits.user.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roarbits.user.entity.CompletedCourse;
import roarbits.user.entity.GraduationType;
import roarbits.user.entity.Profile;
import roarbits.user.entity.User;
import roarbits.user.dto.ProfileDto;
import roarbits.user.repository.ProfileRepository;
import roarbits.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public void updateProfile(Long userId, ProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Profile profile = profileRepository.findByUser(user)
                .orElseGet(() -> {
                    Profile p = Profile.builder().user(user).build();
                    return profileRepository.save(p);
                });

        profile.setUniversity(dto.getUniversity());
        profile.setMajor(dto.getMajor());
        profile.setEnrollmentYear(dto.getEnrollmentYear());
        profile.setGraduationType(dto.getGraduationType());

        profile.getCompletedCourses().clear();
        if (dto.getCompletedCourses() != null) {
            dto.getCompletedCourses().stream()
                    .map(d -> CompletedCourse.builder()
                            .courseCode(d.getCourseCode())
                            .courseTitle(d.getCourseTitle())
                            .profile(profile)
                            .build())
                    .forEach(profile.getCompletedCourses()::add);
        }

        profileRepository.save(profile);
    }

    private Profile findOrCreateProfile(User user) {
        return profileRepository.findByUser(user)
                .orElseGet(() -> profileRepository.save(
                        Profile.builder()
                                .user(user)
                                .build()
                ));
    }

    @Transactional
    public void saveStep1(Long userId, String university, String major) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        Profile profile = findOrCreateProfile(user);
        profile.setUniversity(university);
        profile.setMajor(major);
        profileRepository.save(profile);
    }

    @Transactional
    public void saveStep2(Long userId, Integer enrollmentYear) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        Profile profile = findOrCreateProfile(user);
        profile.setEnrollmentYear(enrollmentYear);
        profileRepository.save(profile);
    }

    @Transactional
    public void saveStep3(Long userId, GraduationType graduationType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        Profile profile = findOrCreateProfile(user);
        profile.setGraduationType(graduationType);
        profileRepository.save(profile);
    }

    @Transactional
    public void saveStep4(Long userId, List<CompletedCourse> courses) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        Profile profile = findOrCreateProfile(user);
        profile.getCompletedCourses().clear();
        courses.forEach(course -> {
            course.setProfile(profile);
            profile.getCompletedCourses().add(course);
        });
        profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public boolean isProfileCompleted(Long userId) {
        return profileRepository.findByUser_Id(userId)
                .map(p -> hasText(p.getUniversity())
                        && hasText(p.getMajor())
                        && p.getEnrollmentYear() != null
                        && p.getGraduationType() != null)
                .orElse(false);
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}

package roarbits.onboarding.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roarbits.onboarding.dto.StepFlags;
import roarbits.user.repository.ProfileRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class OnboardingService {

    private final ProfileRepository profileRepository;

    public StepFlags refreshAndGetFlags(Long userId) {
        boolean s1 = profileRepository.isStep1Done(userId);
        boolean s2 = profileRepository.isStep2Done(userId);
        boolean s3 = profileRepository.isStep3Done(userId);
        boolean s4 = profileRepository.isStep4Done(userId);
        boolean completed = s1 && s2 && s3 && s4;
        return new StepFlags(s1, s2, s3, s4, completed);
    }
}

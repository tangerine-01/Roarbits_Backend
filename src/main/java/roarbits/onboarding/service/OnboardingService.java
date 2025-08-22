package roarbits.onboarding.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roarbits.onboarding.dto.StepFlags;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class OnboardingService {
    public StepFlags getFlags(Long userId) {
        return StepFlags.of(false,false,false,false);
    }
}

package roarbits.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class StepFlags {
    private final boolean step1Done;
    private final boolean step2Done;
    private final boolean step3Done;
    private final boolean step4Done;
    private final boolean completed;
    public boolean isCompleted() { return completed; }
}
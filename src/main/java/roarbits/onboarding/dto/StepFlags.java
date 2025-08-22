package roarbits.onboarding.dto;

public record StepFlags (
    boolean step1,
    boolean step2,
    boolean step3,
    boolean step4,
    boolean allDone
) {
    public static StepFlags of(boolean step1, boolean step2, boolean step3, boolean step4) {
        return new StepFlags(step1, step2, step3, step4, false);
    }
}
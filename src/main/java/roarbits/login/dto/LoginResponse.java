package roarbits.login.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import roarbits.onboarding.dto.StepFlags;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private StepFlags steps;


    public LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.steps = null;
    }
    public void setSteps(StepFlags s) { this.steps = s; }
}

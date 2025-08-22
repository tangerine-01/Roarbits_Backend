package roarbits.login.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.server.ResponseStatusException;
import roarbits.global.api.ApiResponse;
import roarbits.global.api.SuccessCode;

import roarbits.login.auth.jwt.RefreshTokenRepository;
import roarbits.login.dto.LoginRequest;
import roarbits.login.dto.LoginResponse;
import roarbits.login.dto.TokenRefreshRequest;
import roarbits.login.dto.TokenRefreshResponse;
import roarbits.login.service.AuthService;

import roarbits.user.dto.SignUpRequest;
import roarbits.user.dto.SignUpResponse;
import roarbits.user.repository.UserRepository;

import roarbits.onboarding.dto.StepFlags;
import roarbits.onboarding.service.OnboardingService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private final OnboardingService onboardingService;

    // 회원가입
    @PostMapping("/signup")
    public ApiResponse<SignUpResponse> signUp(
           @Valid @RequestBody SignUpRequest req
    ) {
        if (!req.getPassword().equals(req.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        SignUpResponse dto = authService.signUp(req);
        return ApiResponse.onSuccess(SuccessCode.CREATED, dto);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        Long userId = userRepository.findByEmail(loginRequest.getEmail())
                .map(roarbits.user.entity.User::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "<UNK> <UNK> <UNK> <UNK>"));
        StepFlags steps = onboardingService.getFlags(userId);
        response.setSteps(steps);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String email = auth.getName();
                if (email != null && !email.isBlank()) {
                    userRepository.findByEmail(email).ifPresent(u -> {
                        try {
                            refreshTokenRepository.deleteByUser(u);
                        } catch (Exception e) {
                            log.debug("Refresh token delete skip (non-fatal): {}", e.getMessage());
                        }
                    });
                }
            }
            return ApiResponse.onSuccess(SuccessCode.USER_LOGOUT_SUCCESS, null);
        } catch (Exception e) {
            log.debug("Logout soft-fail: {}", e.getMessage());
            return ApiResponse.onSuccess(SuccessCode.USER_LOGOUT_SUCCESS, null);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }





    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<TokenRefreshResponse> reissueToken(@RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.reissueToken(request);
        return ResponseEntity.ok(response);
    }

}
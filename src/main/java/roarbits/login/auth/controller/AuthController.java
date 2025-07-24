package roarbits.login.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import roarbits.global.api.ApiResponse;
import roarbits.global.api.SuccessCode;

import roarbits.login.dto.LoginRequest;
import roarbits.login.dto.LoginResponse;
import roarbits.login.dto.TokenRefreshRequest;
import roarbits.login.dto.TokenRefreshResponse;
import roarbits.login.service.AuthService;

import roarbits.user.dto.SignUpRequest;
import roarbits.user.dto.SignUpResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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


    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<TokenRefreshResponse> reissueToken(@RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.reissueToken(request);
        return ResponseEntity.ok(response);
    }

}
package roarbits.login.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import roarbits.global.api.ApiResponse;
import roarbits.global.api.SuccessCode;

import roarbits.login.auth.jwt.JwtTokenProvider;
import roarbits.login.auth.jwt.RefreshTokenRepository;
import roarbits.login.dto.LoginRequest;
import roarbits.login.dto.LoginResponse;
import roarbits.login.dto.TokenRefreshRequest;
import roarbits.login.dto.TokenRefreshResponse;
import roarbits.login.service.AuthService;

import roarbits.user.dto.SignUpRequest;
import roarbits.user.dto.SignUpResponse;
import roarbits.user.entity.User;
import roarbits.user.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

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

    // 로그아웃
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String accessTokenHeader) {
        String accessToken = accessTokenHeader.replace("Bearer ", "");

        String email = jwtTokenProvider.getUsernameFromToken(accessToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        refreshTokenRepository.deleteByUser(user);

        return ApiResponse.onSuccess(SuccessCode.USER_LOGOUT_SUCCESS, null);
    }


    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<TokenRefreshResponse> reissueToken(@RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.reissueToken(request);
        return ResponseEntity.ok(response);
    }

}
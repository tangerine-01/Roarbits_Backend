package roarbits.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roarbits.login.dto.LoginRequest;
import roarbits.login.dto.LoginResponse;
import roarbits.login.dto.TokenRefreshRequest;
import roarbits.login.dto.TokenRefreshResponse;
import roarbits.login.auth.jwt.JwtTokenProvider;
import roarbits.login.auth.jwt.RefreshToken;
import roarbits.login.auth.jwt.RefreshTokenRepository;
import roarbits.user.dto.SignUpRequest;
import roarbits.user.dto.SignUpResponse;
import roarbits.user.entity.User;
import roarbits.user.repository.UserRepository;
import roarbits.login.mapper.AuthMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    // 1. 회원가입
    @Transactional
    public SignUpResponse signUp(SignUpRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
            User savedUser = userRepository.save(
                    User.builder()
                        .email(req.getEmail())
                        .password(passwordEncoder.encode(req.getPassword()))
                        .build()
            );
            return authMapper.toSignUpResponse(savedUser);


    }


    // 2. 로그인
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken();

        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(newRefreshToken);

        return new LoginResponse(accessToken, refreshTokenValue);
    }

    @Transactional
    public TokenRefreshResponse reissueToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(requestRefreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        RefreshToken storedRefreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("데이터베이스에 존재하지 않는 Refresh Token입니다."));

        if (storedRefreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedRefreshToken);
            throw new IllegalArgumentException("만료된 Refresh Token입니다. 다시 로그인해주세요.");
        }

        User user = storedRefreshToken.getUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);

        return new TokenRefreshResponse(newAccessToken, requestRefreshToken);
    }
}
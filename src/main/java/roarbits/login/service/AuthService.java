package roarbits.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import roarbits.login.exception.DuplicateEmailException;

import java.time.LocalDateTime;
import java.util.Locale;
import java.time.Duration;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    private static String normalizeEmail(String e) {
        return e == null ? "" : e.trim().toLowerCase(Locale.ROOT);
    }

    @Transactional
    public SignUpResponse signUp(SignUpRequest req) {
        final String email = normalizeEmail(req.getEmail());
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateEmailException(req.getEmail());
        }
        try {
            User newUser = userRepository.save(
                    User.builder()
                            .email(email)
                            .password(passwordEncoder.encode(req.getPassword()))
                            .name(req.getName())
                            .build()
            );
            return authMapper.toSignUpResponse(newUser);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException(req.getEmail());
        }
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        final String email = normalizeEmail(req.getEmail());
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new BadCredentialsException("이메일/비번 확인");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("이메일/비번 확인"));
        if (user.isWithdrawn()) {
            throw new BadCredentialsException("탈퇴한 계정입니다");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(auth);
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken();
        long ms = jwtTokenProvider.getRefreshTokenExpirationMs();
        LocalDateTime expiry = LocalDateTime.now().plus(Duration.ofMillis(ms));


        try {
            refreshTokenRepository.findByUser(user).ifPresentOrElse(rt -> {
                rt.setToken(refreshTokenValue);
                rt.setExpiryDate(expiry);
                refreshTokenRepository.save(rt);
            }, () -> {
                RefreshToken rt = RefreshToken.builder()
                        .token(refreshTokenValue)
                        .user(user)
                        .expiryDate(expiry)
                        .build();
                refreshTokenRepository.save(rt);
            });
        } catch (DataIntegrityViolationException race) {
            RefreshToken existing = refreshTokenRepository.findByUser(user)
                    .orElseThrow(() -> race);
            existing.setToken(refreshTokenValue);
            existing.setExpiryDate(expiry);
            refreshTokenRepository.save(existing);
        }

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
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        return new TokenRefreshResponse(newAccessToken, requestRefreshToken);
    }
}

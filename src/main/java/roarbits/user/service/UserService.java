package roarbits.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import roarbits.coursehistory.repository.CourseRepository;
import roarbits.login.auth.jwt.RefreshTokenRepository;
import roarbits.user.dto.SignUpRequest;
import roarbits.user.dto.SignUpResponse;
import roarbits.user.dto.UserResponse;
import roarbits.user.entity.User;
import roarbits.user.repository.ProfileRepository;
import roarbits.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CourseRepository courseRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public SignUpResponse signUp(SignUpRequest req) {
        final String email = normalizeEmail(req.getEmail());

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }

        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }

        if (req.getName() == null || req.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("이미 가입된 이메일입니다");
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .build();

        User saved = userRepository.save(user);

        return SignUpResponse.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .build();
    }

    public UserResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public void deleteUser(String emailRaw) {
        String email = normalizeEmail(emailRaw);
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));
        try { refreshTokenRepository.deleteByUser(user); }
        catch (Exception ignored) {}
        user.markWithdrawn();
    }

    @Transactional
    public void withdraw(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "<UNK> <UNK> <UNK>."));
            try { refreshTokenRepository.deleteByUser(user); }
            catch (Exception ignored) {}
            user.markWithdrawn();}

    private String normalizeEmail(String email) {
        return (email == null) ? null : email.trim().toLowerCase();
    }
}

package roarbits.login.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import roarbits.login.auth.CustomUserDetails;
import roarbits.user.entity.User;
import roarbits.user.repository.UserRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final UserRepository userRepository;

    public long getRefreshTokenExpirationMs() { return refreshTokenExpirationMs; }


    public JwtTokenProvider(
            UserRepository userRepository,
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms:3600000}") long accessTokenExpirationMs,
            @Value("${jwt.refresh-token-expiration-ms:1209600000}") long refreshTokenExpirationMs
    ) {
        if (secret == null || secret.trim().length() < 32) {
            throw new IllegalStateException("jwt.secret 32자 이상");
        }
        this.userRepository = userRepository;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }


    public String generateAccessToken(Authentication authentication) {
        Long userId = extractUserId(authentication.getPrincipal());
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken() {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims c =Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
        return Long.parseLong(c.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Long userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        CustomUserDetails principal = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    private Long extractUserId(Object principal) {
        if (principal instanceof CustomUserDetails cud) {
            return cud.getId();
        }
        if (principal instanceof User u) {
            return u.getId();
        }
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            String email = ud.getUsername();
            return userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElseThrow(() -> new IllegalStateException("User not found by email: " + email));
        }
        throw new IllegalStateException("Unsupported principal type: " + principal.getClass());
    }
}

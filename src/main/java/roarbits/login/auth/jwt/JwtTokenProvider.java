package roarbits.login.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
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

    public String resolveToken(jakarta.servlet.http.HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    public String generateAccessToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .subject(String.valueOf(principal.getId()))
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
        return Long.valueOf(
                Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject()
        );
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Long userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
}

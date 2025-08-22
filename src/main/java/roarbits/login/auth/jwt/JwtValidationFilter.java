package roarbits.login.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final List<String> EXCLUDE = List.of(
            "/", "/error", "/favicon.ico",
            "/api/auth/**",
            "/swagger-ui.html", "/swagger-ui/**",
            "/v3/api-docs", "/v3/api-docs/**",
            "/actuator/health"
    );
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String uri = request.getRequestURI();
        for (String p : EXCLUDE) {
            if (matcher.match(p, uri)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (!StringUtils.hasText(token)) {
            SecurityContextHolder.clearContext();
            unauthorized(response, "Missing Bearer token");
            return;
        }

        try {
            if (!jwtTokenProvider.validateToken(token)) {
                SecurityContextHolder.clearContext();
                unauthorized(response, "Invalid or expired token");
                return;
            }

            Authentication auth = jwtTokenProvider.getAuthentication(token);
            if (auth == null) {
                SecurityContextHolder.clearContext();
                unauthorized(response, "Authentication failed");
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            log.warn("JWT expired: {}", e.getMessage());
            unauthorized(response, "JWT expired");
            return;

        } catch (io.jsonwebtoken.security.SignatureException
                | io.jsonwebtoken.MalformedJwtException
                | io.jsonwebtoken.UnsupportedJwtException e){
            SecurityContextHolder.clearContext();
            log.warn("JWT invalid: {}", e.getMessage());
            unauthorized(response, "JWT invalid");
            return;

        }catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.warn("JWT validation error: {}", e.toString());
            unauthorized(response, "Authentication failed");
            return;
        }
        chain.doFilter(request, response);
    }
    private String resolveToken(HttpServletRequest request) {
        String h = request.getHeader("Authorization");
        if (!StringUtils.hasText(h)) return null;
        if (h.startsWith("Bearer ")) return h.substring(7).trim();
        return null;
    }

    private void unauthorized(HttpServletResponse res, String msg) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
        res.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\"");
        res.getWriter().write("{\"message\":\"" + msg + "\"}");
    }
}

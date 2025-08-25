package roarbits.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import roarbits.login.auth.jwt.JwtTokenProvider;
import roarbits.login.auth.jwt.JwtValidationFilter;
import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private static final String[] SWAGGER_ROOT = {
            "/v3/api-docs", "/swagger-ui.html", "/swagger-ui/**",
            "/api-docs", "/api-docs/**"
    };

    private static final String[] SWAGGER_API = {
            "/api/v3/api-docs/**", "/api/swagger-ui.html", "/api/swagger-ui/**"
    };

    @Bean
    @org.springframework.context.annotation.Primary
    public BCryptPasswordEncoder passwordEncoder() {
        return new BackwardCompatibleBcrypt();
    }

    static class BackwardCompatibleBcrypt extends BCryptPasswordEncoder {

        private final DelegatingPasswordEncoder delegating;

        BackwardCompatibleBcrypt() {
            super();
            var encoders = new java.util.HashMap<String, org.springframework.security.crypto.password.PasswordEncoder>();
            encoders.put("bcrypt", new BCryptPasswordEncoder());
            this.delegating = new org.springframework.security.crypto.password.DelegatingPasswordEncoder("bcrypt", encoders);
            this.delegating.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            if (encodedPassword != null && encodedPassword.startsWith("{")) {
                return delegating.matches(rawPassword, encodedPassword);
            }
            return super.matches(rawPassword, encodedPassword);
        }

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "https://roarbits.com",
                "https://*.netlify.app",
                "https://*.trycloudflare.com"
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean @Order(0)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .securityMatcher("/api/**", "/ai/**")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/v3/api-docs/**").permitAll()
                        .requestMatchers(SWAGGER_API).permitAll()
                        .requestMatchers("/api/interest/**").authenticated()
                        .requestMatchers("/api/course-histories/**").authenticated()
                        .requestMatchers("/ai/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write("{\"message\":\"Unauthorized\"}");
                }));

        http.addFilterBefore(
                new JwtValidationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/", "/error", "/favicon.ico",
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs", "/v3/api-docs/**",
                                "/api-docs", "/api-docs/**",
                                "/actuator/health"
                        ).permitAll()
                        .requestMatchers(SWAGGER_ROOT).permitAll()
                        .anyRequest().permitAll()
                );
        return http.build();
    }

}

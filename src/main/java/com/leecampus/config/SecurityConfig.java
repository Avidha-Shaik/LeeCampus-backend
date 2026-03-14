package com.leecampus.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s ->
                s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\":\"Unauthorized\"}");
                })
                .accessDeniedHandler((req, res, e) -> {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\":\"Forbidden\"}");
                })
            )
            .authorizeHttpRequests(auth -> auth

                // ── OPTIONS preflight ──
                .requestMatchers(
                    org.springframework.http.HttpMethod.OPTIONS, "/**"
                ).permitAll()

                // ── Public endpoints ──
                .requestMatchers(
                    "/leecampus/login",
                    "/leecampus/registerStudent",
                    "/leecampus/admin/login",
                    "/leecampus/faculty/login",
                    "/leecampus/dashboard/**",
                    "/leecampus/stats/**",
                    "/leecampus/leetcode-stats/**",
                    "/error"
                ).permitAll()

                // ── Analytics — ADMIN or FACULTY ──
                .requestMatchers("/leecampus/admin/analytics")
                    .hasAnyRole("ADMIN", "FACULTY")

                // ── Faculty register — ADMIN only ──
                .requestMatchers("/leecampus/faculty/register")
                    .hasRole("ADMIN")

                // ── Admin endpoints — ADMIN only ──
                .requestMatchers("/leecampus/admin/**")
                    .hasRole("ADMIN")

                // ── Faculty endpoints — FACULTY only ──
                .requestMatchers("/leecampus/faculty/**")
                    .hasRole("FACULTY")

                // ── Everything else — any authenticated user ──
                .requestMatchers("/leecampus/**")
                    .hasAnyRole("STUDENT", "ADMIN", "FACULTY")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
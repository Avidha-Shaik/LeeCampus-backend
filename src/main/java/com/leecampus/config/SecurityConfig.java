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

        config.setAllowedOrigins(List.of(
                "https://leecampus-frontend3.onrender.com",
                "http://localhost:5173"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        config.setAllowCredentials(false);
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

            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

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

                // Allow OPTIONS (CORS preflight)
                .requestMatchers(
                        org.springframework.http.HttpMethod.OPTIONS, "/**"
                ).permitAll()

                // Public endpoints
                .requestMatchers(
                        "/leecampus/login",
                        "/leecampus/registerStudent",
                        "/leecampus/registerStudent/**",
                        "/leecampus/allStudents",
                        "/leecampus/getStudent/**",
                        "/leecampus/admin/login",
                        "/leecampus/faculty/login",
                        "/leecampus/dashboard/**",
                        "/leecampus/stats/**",
                        "/leecampus/leetcode-stats/**",
                        "/error"
                ).permitAll()

                // Analytics
                .requestMatchers("/leecampus/admin/analytics")
                        .hasAnyRole("ADMIN", "FACULTY")

                // Faculty register
                .requestMatchers("/leecampus/faculty/register")
                        .hasRole("ADMIN")

                // Admin endpoints
                .requestMatchers("/leecampus/admin/**")
                        .hasRole("ADMIN")

                // Faculty endpoints
                .requestMatchers("/leecampus/faculty/**")
                        .hasRole("FACULTY")

                // All other APIs
                .requestMatchers("/leecampus/**")
                        .hasAnyRole("STUDENT","ADMIN","FACULTY")

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                    jwtFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
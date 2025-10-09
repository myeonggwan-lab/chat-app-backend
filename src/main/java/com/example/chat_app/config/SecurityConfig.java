package com.example.chat_app.config;

import com.example.chat_app.repository.redis.InvalidTokenRepository;
import com.example.chat_app.security.jwt.JwtProvider;
import com.example.chat_app.security.spring_security.CustomLogoutFilter;
import com.example.chat_app.service.RefreshTokenService;
import com.example.chat_app.security.jwt.JwtFilter;
import com.example.chat_app.security.spring_security.CustomJsonLoginFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final InvalidTokenRepository invalidTokenRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 사용
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.formLogin((login) -> login.disable());
        http.httpBasic((auth) -> auth.disable());
        http.logout((logout) -> logout.disable());

        http.addFilterAt(new CustomJsonLoginFilter(
                        authenticationManager(authenticationConfiguration), jwtProvider, objectMapper, refreshTokenService),
                UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new JwtFilter(jwtProvider, invalidTokenRepository), CustomJsonLoginFilter.class);
        http.addFilterAt(new CustomLogoutFilter(jwtProvider, refreshTokenService, invalidTokenRepository), LogoutFilter.class);

        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();

            config.setAllowedOriginPatterns(List.of(
                    "https://chat-app-frontend-viy2d3cq5-myeonggwans-projects.vercel.app",
                    "http://localhost:3000",
                    "https://*.ngrok-free.dev"
            ));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("Authorization","Content-Type")); // 필요한 헤더만
            config.setExposedHeaders(List.of("Authorization"));
            config.setAllowCredentials(true); // 쿠키 + credentials 허용

            return config;
        }));

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/", "/auth/**", "/members", "/members/check", "/ws/chat/**").permitAll()
                .anyRequest().authenticated());

        http.sessionManagement(
                (session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}

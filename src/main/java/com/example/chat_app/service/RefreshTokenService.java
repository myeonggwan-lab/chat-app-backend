package com.example.chat_app.service;

import com.example.chat_app.repository.redis.RefreshTokenRepository;
import com.example.chat_app.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.example.chat_app.config.ConstantConfig.*;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(String key, String loginId, String role) {
        String refreshToken = getRefreshToken(key);
        if(refreshToken == null) {
            refreshToken = jwtProvider.createToken("refresh", loginId, role, REFRESH_EXPIRED_MS);
            refreshTokenRepository.addToken(key, refreshToken, Duration.ofMinutes(3));
        }
        return refreshToken;
    }

    public String getRefreshToken(String key) {
        return refreshTokenRepository.getToken(key);
    }

    public void deleteRefreshToken(String key) {
        refreshTokenRepository.deleteToken(key);
    }
}

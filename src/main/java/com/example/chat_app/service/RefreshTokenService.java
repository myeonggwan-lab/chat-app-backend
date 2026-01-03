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

    /**
     * JWT RefreshToken 생성
     * @param key 키
     * @param loginId JWT Payload에 담을 사용자 아이디
     * @param role JWT Payload에 담을 사용자 권한
     * @return 새로 생성한 RefreshToken
     */
    public String createRefreshToken(String key, String loginId, String role) {
        String refreshToken = getRefreshToken(key);
        if(refreshToken == null) {
            refreshToken = jwtProvider.createToken("refresh", loginId, role, REFRESH_EXPIRED_MS);
            refreshTokenRepository.addToken(key, refreshToken, Duration.ofMinutes(3));
        }
        return refreshToken;
    }

    /**
     * JWT RefreshToken 가져오기
     * @param key 키
     * @return JWT RefreshToken
     */
    public String getRefreshToken(String key) {
        return refreshTokenRepository.getToken(key);
    }

    /**
     * JWT RefreshToken 삭제
     * @param key 키
     */
    public void deleteRefreshToken(String key) {
        refreshTokenRepository.deleteToken(key);
    }
}

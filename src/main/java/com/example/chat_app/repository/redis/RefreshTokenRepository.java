package com.example.chat_app.repository.redis;

import com.example.chat_app.redis.handler.RedisHandler;
import com.example.chat_app.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * JWT RefreshToken을 Redis에서 관리하는 Repository
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final RedisConfig redisConfig;
    private final RedisHandler redisHandler;

    // TODO: 점검 포인트(삭제 유무 확인)
    public void addToken(String key, String value) {
        redisHandler.getValueOperations().set(key, value);
    }

    /**
     * JWT RefreshToken 저장
     * @param key 키
     * @param value 값
     * @param duration 유지시간(TTL)
     */
    public void addToken(String key, String value, Duration duration) {
        redisHandler.getValueOperations().set(key, value, duration);
    }

    /**
     * JWT RefreshToken 가져오기
     * @param key 키
     * @return JWT RefreshToken
     */
    public String getToken(String key) {
        return (String) redisHandler.getValueOperations().get(key);
    }

    /**
     * JWT RefreshToken 삭제
     * @param key 키
     */
    public void deleteToken(String key) {
        redisConfig.redisTemplate().delete(key);
    }
}

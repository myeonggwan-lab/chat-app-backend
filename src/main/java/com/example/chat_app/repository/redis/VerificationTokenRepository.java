package com.example.chat_app.repository.redis;

import com.example.chat_app.config.RedisConfig;
import com.example.chat_app.redis.handler.RedisHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * 검증용 토큰을 Redis에서 관리하는 Repository
 */
@Repository
@RequiredArgsConstructor
public class VerificationTokenRepository {
    private final RedisConfig redisConfig;
    private final RedisHandler redisHandler;

    /**
     * 검증용 토큰 저장
     * @param key 키
     * @param value 값
     */
    public void addToken(String key, String value) {
        redisHandler.getValueOperations().set(key, value);
    }

    /**
     * 검증용 토큰 저장
     * @param key 키
     * @param value 값
     * @param duration 유효시간
     */
    public void addToken(String key, String value, Duration duration) {
        redisHandler.getValueOperations().set(key, value, duration);
    }

    /**
     * 검증용 토큰 가져오기
     * @param key 키
     * @return 검증용 토큰
     */
    public String getToken(String key) {
        return (String) redisHandler.getValueOperations().get(key);
    }

    /**
     * 검증용 토큰 삭제
     * @param key 키
     */
    public void deleteToken(String key) {
        redisConfig.redisTemplate().delete(key);
    }
}

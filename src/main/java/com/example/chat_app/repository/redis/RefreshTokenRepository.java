package com.example.chat_app.repository.redis;

import com.example.chat_app.redis.handler.RedisHandler;
import com.example.chat_app.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final RedisConfig redisConfig;
    private final RedisHandler redisHandler;

    public void addToken(String key, String value) {
        redisHandler.getValueOperations().set(key, value);
    }

    public void addToken(String key, String value, Duration duration) {
        redisHandler.getValueOperations().set(key, value, duration);
    }

    public String getToken(String key) {
        return (String) redisHandler.getValueOperations().get(key);
    }

    public void deleteToken(String key) {
        redisConfig.redisTemplate().delete(key);
    }
}

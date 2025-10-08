package com.example.chat_app.redis.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

// RedisConfig로부터 Redis 설정 정보를 받아서 구현하려는 데이터 타입에 따른 객체를 구성 및 예외 처리를 하는 공통 컴포넌트
@Component
@RequiredArgsConstructor
public class RedisHandler {
//    private final RedisConfig redisConfig;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 리스트에 접근하여 다양한 연산 수행
     * @return ListOperations<String, Object>
     */
    public ListOperations<String, Object> getListOperations() {
        return redisTemplate.opsForList();
    }

    /**
     * 단일 데이터에 접근하여 다양한 연산을 수행
     * @return ValueOperations<String, Object>
     */
    public ValueOperations<String, Object> getValueOperations() {
        return redisTemplate.opsForValue();
    }

    public ZSetOperations<String, Object> getZSetOperations() {
        return redisTemplate.opsForZSet();
    }
}

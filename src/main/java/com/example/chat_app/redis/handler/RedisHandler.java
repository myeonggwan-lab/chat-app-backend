package com.example.chat_app.redis.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

/**
 * RedisHandler
 * RedisConfig로부터 Redis 설정 정보를 받아서 구현하려는 데이터 타입에 따른 객체를 구성 및 예외 처리하는 공통 컴포넌트
 */
@Component
@RequiredArgsConstructor
public class RedisHandler {
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis List 자료구조에 접근(다양한 연산 수행 가능)
     * @return ListOperations<String, Object>
     */
    public ListOperations<String, Object> getListOperations() {
        return redisTemplate.opsForList();
    }

    /**
     * Redis String 자료구조에 접근(다양한 연산을 수행 가능)
     * @return ValueOperations<String, Object>
     */
    public ValueOperations<String, Object> getValueOperations() {
        return redisTemplate.opsForValue();
    }

    /**
     * Redis Set 자료구조에 접근(다양한 연산을 수행 가능)
     * @return SetOperations<String, Object>
     */
    public SetOperations<String, Object> getSetOperations() {
        return redisTemplate.opsForSet();
    }

    /**
     * Redis ZSet 자료구조에 접근(다양한 연산을 수행 가능)
     * @return ZSetOperations<String, Object>
     */
    public ZSetOperations<String, Object> getZSetOperations() {
        return redisTemplate.opsForZSet();
    }
}

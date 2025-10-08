package com.example.chat_app.repository.redis;

import com.example.chat_app.redis.handler.RedisHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class MatchQueueRepository {
    private final RedisHandler redisHandler;
    private static final String QUEUE_NAME = "matchQueue";

    public void addToQueue(String loginId) {
        redisHandler.getZSetOperations().add(QUEUE_NAME, loginId, System.currentTimeMillis());
    }

    public Set<Object> popFromQueue() {
        return redisHandler.getZSetOperations().range(QUEUE_NAME, 0, 1);
    }

    public void removeFromQueue(String... loginIds) {
        // Java varags(...) 사용
        redisHandler.getZSetOperations().remove(QUEUE_NAME, (Object[]) loginIds);
    }

    public Long getQueueSize() {
        return redisHandler.getZSetOperations().size(QUEUE_NAME);
    }
}

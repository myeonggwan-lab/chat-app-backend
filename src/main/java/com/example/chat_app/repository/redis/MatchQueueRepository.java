package com.example.chat_app.repository.redis;

import com.example.chat_app.redis.handler.RedisHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * 회원 매칭과 관련된 데이터를 Redis에서 관리하는 Repository
 */
@Repository
@RequiredArgsConstructor
public class MatchQueueRepository {
    private final RedisHandler redisHandler;
    private static final String QUEUE_NAME = "matchQueue";

    /**
     * 매칭 대기열 큐에 추가
     * @param loginId 매칭에 참여하고자 하는 사용자 아이디
     */
    public void addToQueue(String loginId) {
        redisHandler.getZSetOperations().add(QUEUE_NAME, loginId, System.currentTimeMillis());
    }

    /**
     * 매칭 대기열 큐에서 사용자 2명 가져오기
     * @return Set<Object>
     */
    public Set<Object> popFromQueue() {
        return redisHandler.getZSetOperations().range(QUEUE_NAME, 0, 1);
    }

    /**
     * 매칭 대기열 큐에서 대기 중인 사용자 제거
     * @param loginIds 매칭 대기열 큐에서 제거하고자 하는 사용자 아이디
     */
    public void removeFromQueue(String... loginIds) {
        // Java varargs (...) 사용
        redisHandler.getZSetOperations().remove(QUEUE_NAME, (Object[]) loginIds);
    }

    /**
     * 매칭 대기열 큐에서 대기 중인 사용자 수 가져오기
     * @return Long(매칭 대기열 큐에서 대기 중인 사용자 수)
     */
    public Long getQueueSize() {
        return redisHandler.getZSetOperations().size(QUEUE_NAME);
    }
}

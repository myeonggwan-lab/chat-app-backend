package com.example.chat_app.repository.redis;

import com.example.chat_app.config.RedisConfig;
import com.example.chat_app.dto.ChatRoomDto;
import com.example.chat_app.redis.handler.RedisHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    private final RedisConfig redisConfig;
    private final RedisHandler redisHandler;
    private static final String CHAT_ROOM_NAME = "chatRoom";

    public void addParticipant(ChatRoomDto chatRoom, String loginId) {
        redisHandler.getSetOperations().add(CHAT_ROOM_NAME + ":" + chatRoom.getRoomId(), loginId);
    }

    public Long getParticipantCount(String roomId) {
        return redisHandler.getSetOperations().size(CHAT_ROOM_NAME + ":" + roomId);
    }

    public void deleteParticipant(String roomId, String loginId) {
        redisHandler.getSetOperations().remove(CHAT_ROOM_NAME + ":" + roomId, loginId);
    }

    public void deleteChatRoom(String roomId) {
//        redisHandler.getSetOperations().remove(CHAT_ROOM_NAME + ":" + roomId);
        redisConfig.redisTemplate().delete(CHAT_ROOM_NAME + ":" + roomId);
    }
}

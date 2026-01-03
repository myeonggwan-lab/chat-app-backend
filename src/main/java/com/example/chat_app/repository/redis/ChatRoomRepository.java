package com.example.chat_app.repository.redis;

import com.example.chat_app.config.RedisConfig;
import com.example.chat_app.dto.ChatRoomDto;
import com.example.chat_app.redis.handler.RedisHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 채팅방 관련 데이터를 Redis에서 관리하는 Repository
 */
@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    private final RedisConfig redisConfig;
    private final RedisHandler redisHandler;
    private static final String CHAT_ROOM_NAME = "chatRoom";

    /**
     * 채팅방 참여
     * @param chatRoom 채팅방 정보를 담고 있는 DTO
     * @param loginId 채팅방에 참여하고자 하는 사용자 아이디
     */
    public void addParticipant(ChatRoomDto chatRoom, String loginId) {
        redisHandler.getSetOperations().add(chatRoomKey(chatRoom.getRoomId()), loginId);
    }

    /**
     * 채팅방 참여자 수 가져오기
     * @param roomId 채팅방 식별자
     * @return Long(채팅방 참여자 수)
     */
    public Long getParticipantCount(String roomId) {
        return redisHandler.getSetOperations().size(chatRoomKey(roomId));
    }

    /**
     * 채팅방에서 나가기
     * @param roomId 채팅방 식별자
     * @param loginId 채팅방에서 나가고자 하는 사용자 아이디
     */
    public void deleteParticipant(String roomId, String loginId) {
        redisHandler.getSetOperations().remove(chatRoomKey(roomId), loginId);
    }

    /**
     * 채팅방 삭제
     * @param roomId 채팅방 식별자
     */
    public void deleteChatRoom(String roomId) {
        redisConfig.redisTemplate().delete(chatRoomKey(roomId));
    }

    // 키 생성 메서드
    private String chatRoomKey(String roomId) {
        return CHAT_ROOM_NAME + ":" + roomId;
    }
}

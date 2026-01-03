package com.example.chat_app.service;

import com.example.chat_app.dto.ChatMessageDto;
import com.example.chat_app.entity.Member;
import com.example.chat_app.repository.mysql.MemberRepository;
import com.example.chat_app.repository.redis.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * 채팅 관련 서비스 계층 클래스
 */
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 채팅방 나가기 기능
     * @param roomId 채팅방 고유 식별자
     * @param loginId 채팅방 참여자 로그인 아이디
     */
    public void leaveChatRoom(String roomId, String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new RuntimeException("Member not found"));

        // 해당 채팅방 참여자수가 2명일 때만 시스템 메세지 전송
        if(chatRoomRepository.getParticipantCount(roomId) == 2) {
            ChatMessageDto systemMessage = new ChatMessageDto("System", member.getLoginId() + "님이 채팅방을 나갔습니다.");
            simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId, systemMessage);
        }

        chatRoomRepository.deleteParticipant(roomId, member.getLoginId());

        // 해당 채팅방 참여자수가 0명일 때는 채팅방 삭제
        if(chatRoomRepository.getParticipantCount(roomId) == 0) {
            chatRoomRepository.deleteChatRoom(roomId);
        }
    }
}

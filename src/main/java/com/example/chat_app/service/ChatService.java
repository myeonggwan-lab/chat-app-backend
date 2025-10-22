package com.example.chat_app.service;

import com.example.chat_app.dto.ChatMessageDto;
import com.example.chat_app.entity.Member;
import com.example.chat_app.repository.mysql.MemberRepository;
import com.example.chat_app.repository.redis.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void leaveChatRoom(String roomId, String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new RuntimeException("Member not found"));

        if(chatRoomRepository.getParticipantCount(roomId) == 2) {
            ChatMessageDto systemMessage = new ChatMessageDto("System", member.getLoginId() + "님이 채팅방을 나갔습니다.");
            simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId, systemMessage);
        }

        chatRoomRepository.deleteParticipant(roomId, member.getLoginId());

        if(chatRoomRepository.getParticipantCount(roomId) == 0) {
            chatRoomRepository.deleteChatRoom(roomId);
        }
    }
}

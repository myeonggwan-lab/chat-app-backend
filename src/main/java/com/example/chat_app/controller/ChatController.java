package com.example.chat_app.controller;

import com.example.chat_app.dto.ChatMessageDto;
import com.example.chat_app.entity.Member;
import com.example.chat_app.repository.mysql.MemberRepository;
import com.example.chat_app.repository.redis.ChatRoomRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomRepository chatRoomRepository;
//    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    // ChatController.java
    @MessageMapping("/chat.send/{roomId}")
    @SendTo("/sub/chat/{roomId}")
    public ChatMessageDto sendMessage(
            @DestinationVariable String roomId,
            @Payload ChatMessageDto message, Principal principal) {

        if (principal != null) {
            message.setSender(principal.getName());
        } else {
            message.setSender("Anonymous");
        }

        return message;
    }

    @MessageExceptionHandler({JwtException.class})
    @SendToUser("/user/queue/errors")
    public String handleJwtError(Exception e) {
        return e.getMessage();
    }

    @DeleteMapping("/{roomId}/leave")
    @Transactional
    public void leaveChatRoom(@PathVariable String roomId, Principal principal) {
        Member member = memberRepository.findByLoginId(principal.getName()).orElseThrow(() -> new RuntimeException("Member not found"));

        if(chatRoomRepository.getParticipantCount(roomId) == 2) {
            ChatMessageDto systemMessage = new ChatMessageDto("System", member.getLoginId() + "님이 채팅방을 나갔습니다.");
            simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId, systemMessage);
        }

        chatRoomRepository.deleteParticipant(roomId, member.getLoginId());

        if(chatRoomRepository.getParticipantCount(roomId) == 0) {
            chatRoomRepository.deleteChatRoom(roomId);
        }

//        ChatRoom chatRoom = chatRoomRepository.findByRoomName(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
//        Member member = memberRepository.findByLoginId(principal.getName()).orElseThrow(() -> new RuntimeException("Member not found"));
//
//
//        if(chatRoom.getMemberList().size() == 2) {
//            ChatMessageDto systemMessage = new ChatMessageDto("System", member.getLoginId() + "님이 채팅방을 나갔습니다.");
//
//            simpMessagingTemplate.convertAndSend("/sub/chat/" + roomId, systemMessage);
//        }
//
//        chatRoom.getMemberList().remove(member);
//        member.leaveChatRoom(chatRoom);
//
//        if(chatRoom.getMemberList().isEmpty()) {
//            chatRoomRepository.delete(chatRoom);
//        }
    }
}

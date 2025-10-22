package com.example.chat_app.controller;

import com.example.chat_app.dto.ChatMessageDto;
import com.example.chat_app.service.ChatService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

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
        chatService.leaveChatRoom(roomId, principal.getName());
    }
}

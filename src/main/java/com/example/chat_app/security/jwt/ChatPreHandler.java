package com.example.chat_app.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatPreHandler implements ChannelInterceptor {
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // 메시지 컨텍스트 유지
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization"); // connectHeaders에서 읽음

            if (token != null && token.startsWith("Bearer ")) {
                token = token.split(" ")[1];
            }

            try {
                jwtProvider.validateToken(token);
            } catch (ExpiredJwtException e) {
                throw new JwtException("Expired JWT token");
            } catch (JwtException e) {
                throw new JwtException("Invalid JWT token");
            }

            if(!jwtProvider.getCategory(token).equals("access")) {
                throw new JwtException("Invalid JWT token");
            }

            Authentication authentication = jwtProvider.getAuthentication(token);

            // STOMP 세션에 Authentication 연결
            accessor.setUser(authentication);
        }

        return message;
    }
}

package com.example.chat_app.config;

import com.example.chat_app.security.jwt.ChatPreHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 클래스
 */
@Configuration
@EnableWebSocketMessageBroker // Spring WebSocket 활성화
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final ChatPreHandler chatPreHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat") // handshake Endpoint 등록
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * 메세지를 중계•분배하는 역할을 하는 MessageBroker 설정
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub", "/queue"); // 클라이언트가 구독할 때 붙이는 prefix
        registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트가 보낼 때 붙이는 prefix
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatPreHandler); // ChannelInterceptor 등록
    }
}

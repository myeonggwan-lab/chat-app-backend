package com.example.chat_app.service;

import com.example.chat_app.dto.ChatRoomDto;
import com.example.chat_app.entity.Member;
import com.example.chat_app.repository.mysql.MemberRepository;
import com.example.chat_app.repository.redis.ChatRoomRepository;
import com.example.chat_app.repository.redis.MatchQueueRepository;

import com.example.chat_app.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {
    private final JwtProvider jwtProvider;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MatchQueueRepository matchQueueRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    // synchronized는 JVM 단위 락으로 한번에 하나의 스레드만 메서드 실행하도록 설정
    // 단일 서버의 경우 상관 없지만 다중 서버의 경우 경쟁 상태 위험 -> 분산락 사용 고려
    public synchronized ChatRoomDto joinQueue(String token) {
        String loginId = jwtProvider.getLoginId(token);

        matchQueueRepository.addToQueue(loginId);

        // 대기열에 사용자 추가
        Long queueSize = matchQueueRepository.getQueueSize();

        // 대기열에 존재하는 사용자 수 확인
        if(queueSize >= 2) {
            // 사용자 두명 조회
            Set<Object> candidates = matchQueueRepository.popFromQueue();

            Iterator<Object> iterator = candidates.iterator();
            String userA_LoginId = (String) iterator.next();
            String userB_LoginId = (String) iterator.next();

            matchQueueRepository.removeFromQueue(userA_LoginId, userB_LoginId);

            Member memberA = memberRepository.findByLoginId(userA_LoginId).orElseThrow();
            Member memberB = memberRepository.findByLoginId(userB_LoginId).orElseThrow();

            ChatRoomDto chatRoomDto = new ChatRoomDto("room-" + UUID.randomUUID().toString());

            chatRoomRepository.addParticipant(chatRoomDto, memberA.getLoginId());
            chatRoomRepository.addParticipant(chatRoomDto, memberB.getLoginId());

            // 6️⃣ WebSocket으로 실시간 알림
            simpMessagingTemplate.convertAndSendToUser(
                    userA_LoginId,
                    "/queue/matched-room",
                    chatRoomDto
            );

            simpMessagingTemplate.convertAndSendToUser(
                    userB_LoginId,
                    "/queue/matched-room",
                    chatRoomDto
            );

            return chatRoomDto;
        }

        return null;
    }

    public synchronized void leaveQueue(String token) {
        String loginId = jwtProvider.getLoginId(token);
        matchQueueRepository.removeFromQueue(loginId);
    }
}

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

/**
 * 회원 매칭 관련 서비스 계층
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {
    private final JwtProvider jwtProvider;
    private final SimpMessagingTemplate simpMessagingTemplate; // 서버가 클라이언트에게 메세지를 쉽게 보내도록 돕는 객체
    private final MatchQueueRepository matchQueueRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 매칭 대기열 큐에 참여
     * @param token JWT AccessToken
     * @return 대기열 큐에 두 명 모이면 ChatRoomDto 반환하고 그렇지 않으면 NULL 반환
     */
    public synchronized ChatRoomDto joinQueue(String token) {
        String loginId = jwtProvider.getLoginId(token);

        matchQueueRepository.addToQueue(loginId);

        // 대기열에 사용자 추가
        Long queueSize = matchQueueRepository.getQueueSize();

        // 대기열에 존재하는 사용자 수 확인
        if(queueSize >= 2) {
            Set<Object> candidates = matchQueueRepository.popFromQueue(); // 사용자 두 명 조회

            Iterator<Object> iterator = candidates.iterator();
            String userA_LoginId = (String) iterator.next();
            String userB_LoginId = (String) iterator.next();

            // 매칭 대기열 큐에서 대기 중인 사용자 정보 가져왔으므로 대기열 큐에서 해당 사용자 제거
            matchQueueRepository.removeFromQueue(userA_LoginId, userB_LoginId);

            Member memberA = memberRepository.findByLoginId(userA_LoginId).orElseThrow();
            Member memberB = memberRepository.findByLoginId(userB_LoginId).orElseThrow();

            ChatRoomDto chatRoomDto = new ChatRoomDto("room-" + UUID.randomUUID().toString());

            chatRoomRepository.addParticipant(chatRoomDto, memberA.getLoginId());
            chatRoomRepository.addParticipant(chatRoomDto, memberB.getLoginId());

            // 해당 사용자에게 WebSocket으로 실시간 알림
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

    /**
     * 매칭 대기열 큐에서 나가기
     * @param token JWT AccessToken
     */
    public synchronized void leaveQueue(String token) {
        String loginId = jwtProvider.getLoginId(token);
        matchQueueRepository.removeFromQueue(loginId);
    }
}

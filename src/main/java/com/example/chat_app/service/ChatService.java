package com.example.chat_app.service;

import com.example.chat_app.dto.CreateChatRoomDto;
import com.example.chat_app.entity.Member;
import com.example.chat_app.exception.EntityNotFound;
//import com.example.chat_app.repository.mysql.ChatRoomRepository;
import com.example.chat_app.repository.mysql.MemberRepository;

import com.example.chat_app.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
//    private final ChatRoomRepository chatRoomRepository;

    public void createChatRoom(String token, CreateChatRoomDto createChatRoomDto) {
        String loginId = jwtProvider.getLoginId(token);

        Optional<Member> member = memberRepository.findByLoginId(loginId);

        if(member.isEmpty()) {
            throw new EntityNotFound("NOT_FOUND", "등록된 회원이 존재하지 않습니다.");
        }

//        ChatRoom chatRoom = ChatRoom.builder()
//                .creator(member.get())
//                .roomName(createChatRoomDto.getRoomName())
//                .createdAt(LocalDateTime.now())
//                .build();

//        chatRoomRepository.save(chatRoom);

    }
}

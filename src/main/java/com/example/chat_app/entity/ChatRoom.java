package com.example.chat_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHAT_ROOM_ID")
    private Long id;

    private String roomName;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chatRoom")
    @Builder.Default
    private List<Member> memberList = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> messageList = new ArrayList<>();

}

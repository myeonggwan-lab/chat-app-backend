package com.example.chat_app.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private String sender;
    private String content;
}

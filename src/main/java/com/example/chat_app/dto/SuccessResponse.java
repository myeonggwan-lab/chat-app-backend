package com.example.chat_app.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponse {
    private String status;
    private Object data;
    private String message;

    public static SuccessResponse success(String message, Object data) {
        return SuccessResponse.builder()
                .status("success")
                .data(data)
                .message(message)
                .build();
    }
}

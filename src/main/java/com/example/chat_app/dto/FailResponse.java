package com.example.chat_app.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FailResponse {
    private String status;
    private String code;
    private String message;

    public static FailResponse fail(String code, String message) {
        return FailResponse.builder()
                .status("fail")
                .code(code)
                .message(message)
                .build();
    }
}

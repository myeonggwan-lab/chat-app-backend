package com.example.chat_app.exception;

import lombok.Getter;

@Getter
public class SendMailException extends RuntimeException {
    private final String code;

    public SendMailException(String code, String message) {
        super(message);
        this.code = code;
    }
}

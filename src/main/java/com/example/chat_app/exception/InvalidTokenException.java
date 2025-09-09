package com.example.chat_app.exception;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {
    private final String code;

    public InvalidTokenException(String code, String message) {
        super(message);
        this.code = code;
    }
}

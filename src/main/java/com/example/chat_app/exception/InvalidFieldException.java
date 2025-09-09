package com.example.chat_app.exception;

import lombok.Getter;

@Getter
public class InvalidFieldException extends RuntimeException {
    private final String code;

    public InvalidFieldException(String code, String message) {
        super(message);
        this.code = code;
    }
}

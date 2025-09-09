package com.example.chat_app.exception;

import lombok.Getter;

@Getter
public class MemberAlreadyExistsException extends RuntimeException {
    private final String code;

    public MemberAlreadyExistsException(String code, String message) {
        super(message);
        this.code = code;
    }
}

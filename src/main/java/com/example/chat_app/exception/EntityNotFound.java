package com.example.chat_app.exception;

import lombok.Getter;

@Getter
public class EntityNotFound extends RuntimeException {
    private final String code;

    public EntityNotFound(String code, String message) {
        super(message);
        this.code = code;
    }
}

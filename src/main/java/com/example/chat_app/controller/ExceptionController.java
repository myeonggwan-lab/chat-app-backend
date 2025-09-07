package com.example.chat_app.controller;

import com.example.chat_app.dto.FailResponse;
import com.example.chat_app.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<FailResponse> handleUnAuthorized(UnauthorizedException e) {
        FailResponse response = FailResponse.builder()
                .status("fail")
                .code(e.getCode())
                .message(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

}

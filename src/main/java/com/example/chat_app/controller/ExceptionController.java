package com.example.chat_app.controller;

import com.example.chat_app.dto.FailResponse;
import com.example.chat_app.exception.*;
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

    @ExceptionHandler(MemberAlreadyExistsException.class)
    public ResponseEntity<FailResponse> handleMemberAlreadyExists(MemberAlreadyExistsException e) {
        FailResponse response = FailResponse.builder()
                .status("fail")
                .code(e.getCode())
                .message(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<FailResponse> handleInvalidField(InvalidFieldException e) {
        FailResponse response = FailResponse.builder()
                .status("fail")
                .code(e.getCode())
                .message(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<FailResponse> handleInvalidToken(InvalidTokenException e) {
        FailResponse response = FailResponse.builder()
                .status("fail")
                .code(e.getCode())
                .message(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(SendMailException.class)
    public ResponseEntity<FailResponse> handleMailException(SendMailException e) {
        FailResponse response = FailResponse.builder()
                .status("fail")
                .code(e.getCode())
                .message(e.getMessage()).build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

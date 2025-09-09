package com.example.chat_app.controller;

import com.example.chat_app.dto.MailDto;
import com.example.chat_app.dto.SuccessResponse;
import com.example.chat_app.service.MailService;
import com.example.chat_app.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;
    private final VerificationTokenService verificationTokenService;

    // 인증용 메일 전송
    @PostMapping("/verification-mails")
    public ResponseEntity<SuccessResponse> sendMail(@RequestBody MailDto mailDto) {
        String token = verificationTokenService.createToken(mailDto.getMail());
        mailService.sendVerificationMail(mailDto.getMail(), token);

        return ResponseEntity.
                status(HttpStatus.OK)
                .body(SuccessResponse.success("인증 이메일 전송 완료", mailDto));
    }

    // 메일 인증
    @GetMapping("/verification-mails/verify")
    public ResponseEntity<SuccessResponse> verifyMail(@RequestParam String token) {
        verificationTokenService.verifyToken(token);

        return ResponseEntity.
                status(HttpStatus.OK)
                .body(SuccessResponse.success("메일 인증 성공", new MailDto("verification")));
    }
}

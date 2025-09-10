package com.example.chat_app.controller;

import com.example.chat_app.dto.MailDto;
import com.example.chat_app.dto.SuccessResponse;
import com.example.chat_app.service.MailService;
import com.example.chat_app.service.MemberService;
import com.example.chat_app.service.VerificationTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;
    private final VerificationTokenService verificationTokenService;
    private final MemberService memberService;

    // 인증용 메일 전송
    @PostMapping("/auth/verification")
    public ResponseEntity<SuccessResponse> sendMail(@RequestBody MailDto mailDto) {
        String token = verificationTokenService.createToken(mailDto.getMail());
        mailService.sendVerificationMail(mailDto, token);

        return ResponseEntity.
                status(HttpStatus.OK)
                .body(SuccessResponse.success("인증 이메일 전송 완료", mailDto));
    }

    // 메일 인증
    @GetMapping("/auth/verify")
    public void verifyMail(@RequestParam String token, @RequestParam String purpose,
                                                      HttpServletResponse response) throws IOException {
        String redirectUrl = null;
        boolean valid = verificationTokenService.verifyToken(token);

        if(!valid) {
            response.sendRedirect("http://localhost:3000/verify-fail");
            return;
        }

        switch(purpose) {
            case "verification":
                redirectUrl = "http://localhost:3000/signup?verified=true";
                break;
            case "findLoginId":
                String email = verificationTokenService.getEmailFromToken(token);
                String loginId = memberService.getLoginId(email);
                redirectUrl = "http://localhost:3000/find-login-id-success?loginId=" + loginId;
                break;
            case "resetPassword": // 비밀번호 재설정
                redirectUrl = "http://localhost:3000/reset-password?token=" + token;
                break;
            default:
                redirectUrl = "http://localhost:3000/";
        }

        response.sendRedirect(redirectUrl);
    }
}

package com.example.chat_app.controller;


import com.example.chat_app.dto.SuccessResponse;
import com.example.chat_app.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.example.chat_app.config.ConstantConfig.COOKIE_MAX_AGE;

@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final ReissueService reissueService;

    // JWT 재발급
    @PostMapping("/auth/reissue")
    public ResponseEntity<SuccessResponse> reissueToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = reissueService.validateRefreshToken(request, response);
        String newAccessToken = reissueService.reissueAccessToken(refreshToken);
        String newRefreshToken = reissueService.reissueRefreshToken(refreshToken);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.addHeader("Set-Cookie", createCookie(newRefreshToken).toString());

        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.success("토큰 재발급 완료", null));
    }

    // 쿠키 생성
    private ResponseCookie createCookie(String value) {
        return  ResponseCookie.from("refresh", value)
                .httpOnly(true) // JavaScript에서 접근 불가
                .secure(true) // HTTPS 연결에서만 쿠키 전송
                .sameSite("None") // Cross-Origin 요청에서도 쿠키 전송
                .path("/") // 쿠키가 전체 경로에서 유효
                .maxAge(COOKIE_MAX_AGE) // 쿠키 유효 시간
                .build();
    }
}

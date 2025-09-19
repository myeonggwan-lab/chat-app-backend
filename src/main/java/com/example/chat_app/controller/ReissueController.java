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

    @PostMapping("/auth/reissue")
    public ResponseEntity<SuccessResponse> reissueToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = reissueService.validateToken(request, response);
        String accessToken = reissueService.reissueAccessToken(token);
        String refreshToken = reissueService.reissueRefreshToken(token);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Set-Cookie", createCookie(refreshToken).toString());

        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.success("토큰 재발급 완료", null));
    }
    /**
     * 쿠키 생성 메서드
     */
    private ResponseCookie createCookie(String value) {
        return  ResponseCookie.from("refresh", value)
                .httpOnly(true)
                .secure(false)        // 로컬 테스트
                .sameSite("Lax")     // 크로스도메인 허용
                .path("/")
                .maxAge(COOKIE_MAX_AGE)
                .build();
    }

}

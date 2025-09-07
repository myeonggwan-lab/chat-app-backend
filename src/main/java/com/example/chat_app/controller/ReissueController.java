package com.example.chat_app.controller;


import com.example.chat_app.dto.SuccessResponse;
import com.example.chat_app.service.ReissueService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEntity<SuccessResponse> reissueToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("check point");
        String token = reissueService.validateToken(request, response);
        String accessToken = reissueService.reissueAccessToken(token);
        String refreshToken = reissueService.reissueRefreshToken(token);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createCookie(refreshToken));
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.success("성공적으로 토큰 재발급 완료", null));
    }
    /**
     * 쿠키 생성 메서드
     */
    private Cookie createCookie(String value) {
        Cookie cookie = new Cookie("refresh", value);

        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);

        return cookie;
    }

}

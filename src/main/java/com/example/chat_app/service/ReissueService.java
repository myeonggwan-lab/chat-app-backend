package com.example.chat_app.service;

import com.example.chat_app.config.ConstantConfig;
import com.example.chat_app.exception.UnauthorizedException;
import com.example.chat_app.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.example.chat_app.config.ConstantConfig.ACCESS_EXPIRED_MS;


@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public String validateToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if(cookies == null) {
            throw new UnauthorizedException("UNAUTHORIZED", "필요한 인증 정보가 존재하지 않습니다.");
        }

        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")) {
                refreshToken = cookie.getValue();
            }
        }

        if(refreshToken == null) {
            throw new UnauthorizedException("UNAUTHORIZED", "필요한 인증 정보가 존재하지 않습니다.");
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch(ExpiredJwtException e) {
            throw new UnauthorizedException("EXPIRED", "토큰이 만료되었습니다.");
        }

        if(!jwtUtil.getCategory(refreshToken).equals("refresh")) {
            throw new UnauthorizedException("UNAUTHORIZED", "토큰이 유효하지 않습니다.");
        }

        return refreshToken;
    }

    public String reissueAccessToken(String refreshToken) {
        String loginId = jwtUtil.getLoginId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        return jwtUtil.createJwt("access", loginId, role, ACCESS_EXPIRED_MS);
    }

    public String reissueRefreshToken(String refreshToken) {
        String loginId = jwtUtil.getLoginId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        String key = "refresh:" + loginId;

        if(refreshTokenService.getRefreshToken(key) != null) {
            refreshTokenService.deleteRefreshToken(key);
        }

        return refreshTokenService.createRefreshToken(key, loginId, role);
    }
}

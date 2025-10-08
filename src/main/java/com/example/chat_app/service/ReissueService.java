package com.example.chat_app.service;


import com.example.chat_app.exception.UnauthorizedException;
import com.example.chat_app.security.jwt.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.example.chat_app.config.ConstantConfig.ACCESS_EXPIRED_MS;
import static com.example.chat_app.utils.ResponseUtils.sendFailResponse;


@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JwtProvider jwtProvider;
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
            jwtProvider.validateToken(refreshToken);
        } catch(ExpiredJwtException e) {
            throw new UnauthorizedException("EXPIRED", "토큰이 만료되었습니다.");
        } catch (JwtException e) {
            sendFailResponse(response, "UNAUTHORIZED", "토큰이 유효하지 않습니다.", HttpServletResponse.SC_UNAUTHORIZED);
        }

        if(!jwtProvider.getCategory(refreshToken).equals("refresh")) {
            throw new UnauthorizedException("UNAUTHORIZED", "토큰이 유효하지 않습니다.");
        }

        return refreshToken;
    }

    public String reissueAccessToken(String refreshToken) {
        String loginId = jwtProvider.getLoginId(refreshToken);
        String role = jwtProvider.getRole(refreshToken);

        return jwtProvider.createToken("access", loginId, role, ACCESS_EXPIRED_MS);
    }

    public String reissueRefreshToken(String refreshToken) {
        String loginId = jwtProvider.getLoginId(refreshToken);
        String role = jwtProvider.getRole(refreshToken);
        String key = "refresh:" + loginId;

        if(refreshTokenService.getRefreshToken(key) != null) {
            refreshTokenService.deleteRefreshToken(key);
        }

        return refreshTokenService.createRefreshToken(key, loginId, role);
    }
}

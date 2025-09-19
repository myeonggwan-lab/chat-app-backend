package com.example.chat_app.security.spring_security;

import com.example.chat_app.repository.redis.InvalidTokenRepository;
import com.example.chat_app.security.jwt.JwtUtil;
import com.example.chat_app.service.RefreshTokenService;
import com.example.chat_app.utils.ResponseUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final InvalidTokenRepository invalidTokenRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorization.split(" ")[1];


        String requestURI = request.getRequestURI();

        if(!requestURI.matches("^/auth/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        String method = request.getMethod();
        if(!method.equals("POST")){
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            ResponseUtils.sendFailResponse(response, "UNAUTHORIZED", "필요한 인증 정보가 없습니다.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")) {
                refreshToken = cookie.getValue();
            }
        }

        if(refreshToken == null) {
            ResponseUtils.sendFailResponse(response, "UNAUTHORIZED", "필요한 인증 정보가 없습니다.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            jwtUtil.isExpired(refreshToken);
        }catch(ExpiredJwtException e) {
            ResponseUtils.sendFailResponse(response, "EXPIRED", "토큰이 만료되었습니다.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(refreshToken);

        if(!category.equals("refresh")) {
            ResponseUtils.sendFailResponse(response, "UNAUTHORIZED", "유효하지 않은 토큰입니다.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String loginId = jwtUtil.getLoginId(refreshToken);

        try {
            long expiration = jwtUtil.getExpiration(accessToken);
            long ttl = expiration - System.currentTimeMillis();

            invalidTokenRepository.addToken("access:" + loginId, accessToken, Duration.ofMillis(ttl));
            refreshTokenService.deleteRefreshToken("refresh:" + loginId);
        } catch (ExpiredJwtException e) {
            refreshTokenService.deleteRefreshToken("refresh:" + loginId);

            Cookie cookie = new Cookie("refresh", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");

            response.addCookie(cookie);
            ResponseUtils.sendSuccessResponse(response, "성공적으로 로그아웃 되었습니다.", null, HttpServletResponse.SC_OK);
        }

    }
}

package com.example.chat_app.security.jwt;

import com.example.chat_app.repository.redis.InvalidTokenRepository;
import com.example.chat_app.security.spring_security.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.chat_app.utils.ResponseUtils.*;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final InvalidTokenRepository invalidTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        try {
            jwtProvider.validateToken(token);
        } catch (ExpiredJwtException e) {
            sendFailResponse(response, "EXPIRED", "토큰이 만료되었습니다.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (JwtException e) {
            sendFailResponse(response, "UNAUTHORIZED", "토큰이 유효하지 않습니다.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if(!jwtProvider.getCategory(token).equals("access")) {
            sendFailResponse(response, "UNAUTHORIZED", "토큰이 유효하지 않습니다.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 인증 객체 생성
        Authentication auth = jwtProvider.getAuthentication(token);

        // 블랙리스트 체크
        String key = "access:" + ((CustomUserDetails) auth.getPrincipal()).getUsername();
        String invalidToken = invalidTokenRepository.getToken(key);

        if (invalidToken != null && invalidToken.equals(token)) {
            sendFailResponse(response, "UNAUTHORIZED", "사용할 수 없는 토큰입니다.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // SecurityContext에 등록
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}

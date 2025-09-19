package com.example.chat_app.security.jwt;

import com.example.chat_app.enums.Role;
import com.example.chat_app.dto.MemberDto;
import com.example.chat_app.repository.redis.InvalidTokenRepository;
import com.example.chat_app.security.spring_security.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.chat_app.utils.ResponseUtils.*;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final InvalidTokenRepository invalidTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        try{
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            sendFailResponse(response, "EXPIRED", "토큰이 만료되었습니다.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(token);

        if(!category.equals("access")) {
            sendFailResponse(response, "UNAUTHORIZED", "토큰이 유효하지 않습니다.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String loginId = jwtUtil.getLoginId(token);
        String role = jwtUtil.getRole(token);
        String key = "access:" + loginId;

        String invalidToken = invalidTokenRepository.getToken(key);

        if(invalidToken != null && invalidToken.equals(token)) {
            sendFailResponse(response, "UNAUTHORIZED", "사용할 수 없는 토큰입니다.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        MemberDto memberDto = MemberDto.builder()
                .loginId(loginId)
                .role(Role.valueOf(role))
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(memberDto);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}

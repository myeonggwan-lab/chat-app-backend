package com.example.chat_app.security.jwt;

import com.example.chat_app.dto.MemberDto;
import com.example.chat_app.enums.Role;
import com.example.chat_app.security.spring_security.CustomUserDetails;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Component
public class JwtProvider {
    private final SecretKey secretKey;

    public JwtProvider(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), // 비밀키 문자열 -> 바이트 배열
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public String createToken(String category, String loginId, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("loginId", loginId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    // 토큰 검증
    public void validateToken(String token) {
        isExpired(token);
    }

    // Claims 가져오기
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // JWT 만료가 되지 않았을 때는 true 값이 반환, 만료가 되면 false 대신 파싱 과정에서 예외가 던져져서 ExpiredJwtException 예외가 발생
    public Boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public String getLoginId(String token) {
        return getClaims(token).get("loginId", String.class);
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String getCategory(String token) {
        return getClaims(token).get("category", String.class);
    }

    public long getExpiration(String token) {
        return getClaims(token).getExpiration().getTime();
    }

    // Authentication 객체 생성 (스프링 시큐리티 연계)
    public Authentication getAuthentication(String token) {
        String loginId = getLoginId(token);
        String role = getRole(token);

        MemberDto memberDto = MemberDto.builder()
                .loginId(loginId)
                .role(Role.valueOf(role))
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(memberDto);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}

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


/**
 * JWT 관련 동작 정의<br/>
 * - 토큰 생성<br/>
 * - 토큰 파싱 및 검증<br/>
 * - 인증 정보 추출<br/>
 * - Spring Security Authentication 생성
 */
@Component
public class JwtProvider {
    private final SecretKey secretKey;

    public JwtProvider(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), // 비밀키 문자열 → 바이트 배열
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    /**
     * JWT 생성
     * @param category 카테고리(Access, Refresh)
     * @param loginId 사용자 아이디
     * @param role 사용자 권한
     * @param expiredMs 토큰 만료시간
     * @return JWT 문자열
     */
    public String createToken(String category, String loginId, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("loginId", loginId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 발행시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료시간
                .signWith(secretKey) // 비밀키로 서명
                .compact(); // Header, Payload, Signature를 결합해 하나의 JWT 문자열 생성
    }

    /**
     * JWT 검증
     * @param token JWT AccessToken
     */
    public void validateToken(String token) {
        isExpired(token);
    }

    /**
     * JWT Claims 가져오기
     * @param token JWT AccessToken
     * @return Claims
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * JWT 만료 여부 확인
     * @param token JWT AccessToken
     * @return Boolean(JWT 만료 여부)
     */
    // JWT 만료가 되지 않았을 때는 true 값이 반환, 만료가 되면 false 대신 파싱 과정에서 예외가 던져져서 ExpiredJwtException 예외가 발생
    public Boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    /**
     * JWT Payload에 담긴 아이디 가져오기
     * @param token JWT AccessToken
     * @return String(아이디)
     */
    public String getLoginId(String token) {
        return getClaims(token).get("loginId", String.class);
    }

    /**
     * JWT Payload에 담긴 사용자 권한 가져오기
     * @param token JWT AccessToken
     * @return String(사용자 권한)
     */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * JWT Payload에 담긴 JWT 카테고리 가져오기
     * @param token JWT AccessToken
     * @return String(카테고리)
     */
    public String getCategory(String token) {
        return getClaims(token).get("category", String.class);
    }

    /**
     * JWT Payload에 담긴 JWT 만료시간 가져오기
     * @param token JWT AccessToken
     * @return JWT 만료시간
     */
    public long getExpiration(String token) {
        return getClaims(token).getExpiration().getTime();
    }

    /**
     * Spring Security Authentication 생성
     * @param token JWT AccessToken
     * @return Authentication
     */
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

package com.example.chat_app.security.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// JWT Util 클래스
@Component
public class JwtUtil {
    private final SecretKey secretkey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) { // application.properties 파일에 있는 비밀키 사용
        // new SecretKeySpec(..., algorithm)
        // 바이트 배열을 기반으로 대칭키 객체 생성
        secretkey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), // 비밀키 문자열 -> 바이트 배열
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public String getCategory(String token) {
        return Jwts.parser() // JwtParser 생성
                .verifyWith(secretkey).build() // JwtParser 통해 검증 시 사용할 비밀키 설정 후 JwtParser 빌드
                .parseSignedClaims(token) // 토큰을 파싱하여 서명을 검증
                .getPayload() // Payload 추출
                .get("category", String.class); // Payload에서 claim 추출
    }

    public String getLoginId(String token) {
        return Jwts.parser()
                .verifyWith(secretkey).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("loginId", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretkey).build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    // JWT 만료가 되지 않았을 때는 true 값이 반환, 만료가 되면 false 대신 파싱 과정에서 예외가 던져져서 ExpiredJwtException 예외가 발생
    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretkey).build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration().before(new Date());
    }

    public long getExpiration(String token) {
        return Jwts.parser()
                .verifyWith(secretkey).build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration().getTime();
    }

    public String createJwt(String category, String loginId, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("loginId", loginId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretkey)
                .compact();
    }

}

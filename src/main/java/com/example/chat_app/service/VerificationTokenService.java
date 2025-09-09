package com.example.chat_app.service;

import com.example.chat_app.exception.InvalidTokenException;
import com.example.chat_app.repository.redis.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public String createToken(String mail) {
        String token = UUID.randomUUID().toString();
        String key = "verification:token:" + token;

        verificationTokenRepository.addToken(key, mail, Duration.ofMinutes(3));

        return token;
    }

    public void verifyToken(String token) {
        String key = "verification:token:" + token;
        String mail = verificationTokenRepository.getToken(key);

        if(mail == null) {
            throw new InvalidTokenException("INVALID_TOKEN", "유효하지 않은 토큰입니다.");
        }
    }
}

package com.example.chat_app.service;

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

    public boolean verifyToken(String token) {
        String email = getEmailFromToken(token);

        return email != null;
    }

    public String getEmailFromToken(String token) {
        String key = "verification:token:" + token;

        return verificationTokenRepository.getToken(key);
    }
}

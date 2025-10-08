package com.example.chat_app.service;

import com.example.chat_app.repository.redis.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public String createToken(String mail) {
        int token = (int)(Math.random() * 900000) + 100000; // 100000~999999
        String key = "verification:token:" + token;

        verificationTokenRepository.addToken(key, mail, Duration.ofMinutes(3));

        return String.valueOf(token);
    }

    public boolean verifyToken(String token) {
        String mail = getMailFromToken(token);

        return mail != null;
    }

    public boolean verifyToken(String token, String mail) {
        return mail.equals(getMailFromToken(token));
    }

    public String getMailFromToken(String token) {
        String key = "verification:token:" + token;

        return verificationTokenRepository.getToken(key);
    }
}

package com.example.chat_app.service;

import com.example.chat_app.repository.redis.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    /**
     * 인증용 토큰 생성
     * @param mail 사용자가 입력한 메일 정보
     * @return 생성된 인증용 토큰 반환
     */
    public String createToken(String mail) {
        int token = (int)(Math.random() * 900000) + 100000; // 100000~999999
        String key = "verification:token:" + token;

        // 토큰 검증을 위해 토큰 생성 후 Redis에 저장
        verificationTokenRepository.addToken(key, mail, Duration.ofMinutes(3));

        return String.valueOf(token);
    }

    /**
     * 사용자가 입력한 토큰 검증
     * @param token 검증할 토큰 정보
     * @return boolean(검증 결과 반환)
     */
    public boolean verifyToken(String token) {
        String mail = getMailFromToken(token);

        return mail != null;
    }

    // TODO: 이메일 수정 시 토큰 검증
    public boolean verifyToken(String token, String mail) {
        return mail.equals(getMailFromToken(token));
    }

    /**
     * 토큰에 해당하는 메일 정보 가져오기
     * @param token 토큰 정보
     * @return String(메일)
     */
    public String getMailFromToken(String token) {
        String key = "verification:token:" + token;

        return verificationTokenRepository.getToken(key);
    }
}

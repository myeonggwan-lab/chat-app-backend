package com.example.chat_app.config;

/**
 * Access Token, Refresh Token, Cookie 만료시간 설정
 */
public class ConstantConfig {
    public static final Long ACCESS_EXPIRED_MS = 1000L * 60;
    public static final Long REFRESH_EXPIRED_MS = 3000L * 60;
    public static final int COOKIE_MAX_AGE = 24 * 60 * 60;
}

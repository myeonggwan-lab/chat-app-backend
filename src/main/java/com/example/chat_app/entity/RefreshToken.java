package com.example.chat_app.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "refreshToken", timeToLive = 3600)
public class RefreshToken {
    @Id
    private String id;
    @Indexed
    private String token;
    @TimeToLive
    private Long expiration;
}

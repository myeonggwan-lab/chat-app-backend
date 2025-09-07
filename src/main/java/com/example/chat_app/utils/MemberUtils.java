package com.example.chat_app.utils;

import com.example.chat_app.dto.MemberDto;
import com.example.chat_app.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberUtils {
    private final PasswordEncoder passwordEncoder;

    public Member toMember(MemberDto memberDto) {
        return Member.builder()
                .loginId(memberDto.getLoginId())
                .username(memberDto.getUsername())
                .nickname(memberDto.getNickname())
                .email(memberDto.getEmail())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .build();
    }

    public MemberDto toMemberDto(Member member) {
        return MemberDto.builder()
                .username(member.getUsername())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .role(member.getRole())
                .build();
    }
}

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
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .mail(memberDto.getMail())
                .username(memberDto.getUsername())
                .nickname(memberDto.getNickname())
                .build();
    }

    public MemberDto toMemberDto(Member member) {
        return MemberDto.builder()
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .mail(member.getMail())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();
    }
}

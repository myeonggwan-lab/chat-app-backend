package com.example.chat_app.utils;

import com.example.chat_app.dto.MemberDto;
import com.example.chat_app.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 회원 관련 편의 클래스
 */
@Component
@RequiredArgsConstructor
public class MemberUtils {
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 DTO를 회원 Entity로 변환
     * @param memberDto 회원 정보를 담고 있는 DTO
     * @return Member
     */
    public Member toMember(MemberDto memberDto) {
        return Member.builder()
                .loginId(memberDto.getLoginId())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .mail(memberDto.getMail())
                .username(memberDto.getUsername())
                .nickname(memberDto.getNickname())
                .build();
    }

    /**
     * 회원 Entity를 회원 DTO로 변환
     * @param member 회원 Entity
     * @return MemberDto
     */
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

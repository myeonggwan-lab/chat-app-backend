package com.example.chat_app.service;

import com.example.chat_app.dto.MemberDto;

import com.example.chat_app.repository.mysql.MemberRepository;
import com.example.chat_app.utils.MemberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberUtils memberUtils;
    public void registerMember(MemberDto memberDto) {
        if(!memberRepository.existsByLoginId(memberDto.getLoginId())) {
            memberRepository.save(memberUtils.toMember(memberDto));
        }
    }
}

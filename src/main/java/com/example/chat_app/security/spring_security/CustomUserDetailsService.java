package com.example.chat_app.security.spring_security;

import com.example.chat_app.entity.Member;
import com.example.chat_app.repository.mysql.MemberRepository;
import com.example.chat_app.utils.MemberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final MemberUtils memberUtils;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new UsernameNotFoundException(loginId));
        return new CustomUserDetails(memberUtils.toMemberDto(member));
    }
}

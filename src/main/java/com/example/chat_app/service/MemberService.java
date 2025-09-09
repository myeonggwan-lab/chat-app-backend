package com.example.chat_app.service;

import com.example.chat_app.dto.MemberDto;

import com.example.chat_app.exception.InvalidFieldException;
import com.example.chat_app.exception.MemberAlreadyExistsException;
import com.example.chat_app.repository.mysql.MemberRepository;
import com.example.chat_app.utils.MemberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberUtils memberUtils;

    // 영어 대소문자만, 4~12자
    private static final Pattern LOGIN_ID_PATTERN = Pattern.compile("^[a-zA-Z]{4,12}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^(?=.*[a-zA-Z가-힣])[a-zA-Z0-9가-힣]{2,12}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[가-힣]{2,10}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,16}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");

    public void registerMember(MemberDto memberDto) {
        if(memberRepository.findByLoginId(memberDto.getLoginId()).isPresent()) {
            throw new MemberAlreadyExistsException("EXISTED_MEMBER", "이미 존재하는 회원입니다.");
        }

        memberRepository.save(memberUtils.toMember(memberDto));
    }

    public void checkLoginId(String loginId) {
        if(!LOGIN_ID_PATTERN.matcher(loginId).matches()) {
            throw new InvalidFieldException("INVALID_LOGIN_ID", "아이디는 영어 4~12자만 가능합니다.");
        }


        if(memberRepository.existsByLoginId(loginId)) {
            throw new MemberAlreadyExistsException("DUPLICATED_LOGIN_ID", "이미 사용 중인 아이디입니다.");
        }
    }

    public void checkPassword(String password) {
        if(!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidFieldException("INVALID_PASSWORD", "비밀번호는 영어와 숫자 혼합해서 8~16자만 가능합니다.");
        }
    }

    public void checkEmail(String email) {
        if(!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidFieldException("INVALID_EMAIL", "이메일 형식이 유효하지 않습니다.");
        }
    }

    public void checkUsername(String username) {
        if(!USERNAME_PATTERN.matcher(username).matches()) {
            throw new InvalidFieldException("INVALID_USERNAME", "이름은 한글만 허용되며 2~10자여야 합니다.");
        }
    }

    public void checkNickname(String nickname) {
        if(!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new InvalidFieldException("INVALID_NICKNAME", "닉네임은 한글, 영어, 숫자 가능하며 2~12자여야 합니다.");
        }

        if(memberRepository.existsByNickname(nickname)) {
            throw new MemberAlreadyExistsException("DUPLICATED_NICKNAME", "이미 사용 중인 닉네임입니다.");
        }
    }
}

package com.example.chat_app.controller;

import com.example.chat_app.dto.MemberDto;
import com.example.chat_app.dto.SuccessResponse;
import com.example.chat_app.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.chat_app.dto.SuccessResponse.*;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping("/members")
    public ResponseEntity<SuccessResponse> registerMember(@RequestBody MemberDto member) {
        memberService.registerMember(member);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(success("회원가입 성공", null));
    }
}

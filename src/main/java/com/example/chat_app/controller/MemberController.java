package com.example.chat_app.controller;

import com.example.chat_app.dto.MemberDto;
import com.example.chat_app.dto.ResetPasswordDto;
import com.example.chat_app.dto.SuccessResponse;
import com.example.chat_app.dto.UpdateMemberFieldDto;
import com.example.chat_app.exception.InvalidFieldException;
import com.example.chat_app.exception.InvalidTokenException;
import com.example.chat_app.service.MemberService;
import com.example.chat_app.service.VerificationTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.chat_app.dto.SuccessResponse.*;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final VerificationTokenService verificationTokenService;

    // 회원가입
    @PostMapping("/members")
    public ResponseEntity<SuccessResponse> registerMember(@RequestBody MemberDto member) {
        memberService.registerMember(member);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(success("회원가입 성공", null));
    }

    // 회원가입 시 입력해야 하는 필드 검증
    @GetMapping("/members/check")
    public ResponseEntity<SuccessResponse> checkField(@RequestParam String field, @RequestParam String value) {
        switch (field) {
            case "loginId":
                memberService.checkLoginId(value);
                break;
            case "password":
                memberService.checkPassword(value);
                break;
            case "email":
                memberService.checkEmail(value);
                break;
            case "username":
                memberService.checkUsername(value);
                break;
            case "nickname":
                memberService.checkNickname(value);
                break;
            default:
                throw new InvalidFieldException("INVALID_FIELD", "필드가 유효하지 않습니다.");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.success("사용 가능합니다.", null));
    }

    // 비밀번호 재설정
    @PatchMapping("/auth/reset-password")
    public ResponseEntity<SuccessResponse> resetPassword(@RequestParam String token, @RequestBody ResetPasswordDto resetPasswordDto) {
        if(!verificationTokenService.verifyToken(token)) {
            throw new InvalidTokenException("INVALID_TOKEN", "토큰이 유효하지 않습니다.");
        }

        String email = verificationTokenService.getEmailFromToken(token);
        memberService.resetPassword(email, resetPasswordDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.success("비밀번호 재설정 완료", null));
    }

    // 회원 수정
    @PatchMapping("/members")
    public ResponseEntity<SuccessResponse> updateMemberField(@RequestBody UpdateMemberFieldDto memberFieldDto, HttpServletRequest request) {
        String token = request.getHeader("Authorization").split(" ")[1];

        memberService.updateMemberField(token, memberFieldDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.success("회원 수정 완료", null));
    }
}

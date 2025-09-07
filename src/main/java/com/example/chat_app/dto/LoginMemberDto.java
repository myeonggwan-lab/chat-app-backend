package com.example.chat_app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LoginMemberDto {
    String loginId;
    String password;

    public LoginMemberDto(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}

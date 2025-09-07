package com.example.chat_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 제외
public class MemberDto {
    private String username;
    private String nickname;
    private String email;
    private String loginId;
    private String password;
    @Builder.Default
    private Role role = Role.ROLE_USER;
}

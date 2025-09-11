package com.example.chat_app.entity;

import com.example.chat_app.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String loginId;
    private String password;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    public void updateUsername(String username) { this.username = username; }

    public void updateNickname(String nickname) { this.nickname = nickname; }

    public void updateEmail(String email) { this.email = email; }

    // 비밀번호 변경
    public void updatePassword(String password) {
        this.password = password;
    }
}

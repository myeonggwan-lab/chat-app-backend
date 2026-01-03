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
    private String mail;
    private String loginId;
    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    /**
     * 사용자 이름 수정
     * @param username 수정하고자 하는 이름
     */
    public void updateUsername(String username) { this.username = username; }

    /**
     * 사용자 닉네임 수정
     * @param nickname 수정하고자 하는 닉네임
     */
    public void updateNickname(String nickname) { this.nickname = nickname; }

    // TODO: 이메일 수정
    /**
     * 사용자 메일 수정
     * @param email 수정하고자 하는 메일
     */
    public void updateMail(String email) { this.mail = email; }

    /**
     * 사용자 비밀번호 수정
     * @param password 수정하고자 하는 비밀번호
     */
    public void updatePassword(String password) {
        this.password = password;
    }
}

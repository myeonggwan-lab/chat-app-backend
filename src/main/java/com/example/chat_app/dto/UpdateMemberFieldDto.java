package com.example.chat_app.dto;

import com.example.chat_app.enums.MemberField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberFieldDto {
    private String value;
    private MemberField memberField;
}

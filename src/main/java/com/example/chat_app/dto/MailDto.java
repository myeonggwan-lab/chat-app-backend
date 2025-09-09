package com.example.chat_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MailDto {
    private String mail;
    private String purpose;

    public MailDto(String purpose)  {
        this.mail = null;
        this.purpose = purpose;
    }
}

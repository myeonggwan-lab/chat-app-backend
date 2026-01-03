package com.example.chat_app.service;

import com.example.chat_app.dto.MailDto;
import com.example.chat_app.exception.SendMailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    /**
     * 인증용 메일 전송
     * @param mailDto 메일 정보(메일, 목적)
     * @param token 토큰
     */
    public void sendVerificationMail(MailDto mailDto, String token) {
        String link = "http://localhost:8080/auth/verify?token=" + token + "&purpose=" + mailDto.getPurpose();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 목적에 따라 인증 유형 다르게 처리
            if(mailDto.getPurpose().equals("update")) {
                getNumberMessage(helper, mailDto.getMail(), token);
            } else {
                getLinkMessage(helper, mailDto.getMail(), link);
            }

            mailSender.send(message); // 메일 전송
        } catch(MessagingException e) {
            throw new SendMailException("SEND_MAIL_ERROR", "인증 메일 전송 중 오류가 발생했습니다.");
        }
    }

    /**
     * 메일 인증 유형 - 링크 인증
     */
    private void getLinkMessage(MimeMessageHelper helper, String mail, String link) throws MessagingException {
        helper.setTo(mail);
        helper.setSubject("Myeonggwan-Lab");
        helper.setText("<h1 style='text-align: center;'>Myeonggwan-Lab</h1>" +
                "<h3 style='text-align: center;'><a href='" + link + "'>인증 링크</a></h3>", true);
    }

    /**
     * 메일 인증 유형 - 토큰 입력
     */
    private void getNumberMessage(MimeMessageHelper helper, String mail, String token) throws MessagingException {
        helper.setTo(mail);
        helper.setSubject("Myeonggwan-Lab");
        helper.setText("<h1 style='text-align: center;'>Myeonggwan-Lab</h1>" +
                "<h3 style='text-align: center;'><span>"+ token +"</span></h3>", true);
    }
}

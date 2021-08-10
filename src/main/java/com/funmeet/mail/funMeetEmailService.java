package com.funmeet.mail;

import com.funmeet.form.EmailMessageForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Profile("db")
@RequiredArgsConstructor
@Component
@Slf4j
public class funMeetEmailService implements EmailService{

    private final JavaMailSender javaMailSender;

    @Override
    public void send(EmailMessageForm emailMessageForm) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage,false,"UTF-8");
            mimeMessageHelper.setTo(emailMessageForm.getTo());
            mimeMessageHelper.setSubject(emailMessageForm.getSubject());
            mimeMessageHelper.setText(emailMessageForm.getText());

            javaMailSender.send(mimeMessage);
            log.info("success send to email : {}",emailMessageForm.getTo());
        } catch (MessagingException e) {
            log.info("fail send to email : {}", emailMessageForm.getTo());
        }
    }
}

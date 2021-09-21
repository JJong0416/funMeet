package com.funmeet.infra.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Profile("db")
@RequiredArgsConstructor
@Component
@Slf4j
public class FunMeetEmailService implements EmailService{

    private final JavaMailSender javaMailSender;

    @Async
    @Override
    public void send(EmailMessageForm emailMessageForm) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage,false,"UTF-8");
            mimeMessageHelper.setTo(emailMessageForm.getTo());
            mimeMessageHelper.setSubject(emailMessageForm.getSubject());
            mimeMessageHelper.setText(emailMessageForm.getText(),true);
            javaMailSender.send(mimeMessage);

            log.info("success send to email : {}",emailMessageForm.getTo());
        } catch (MessagingException e) {
            log.info("fail send to email : {}", emailMessageForm.getTo());
            throw new RuntimeException(e);
        }
    }
}

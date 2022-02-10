package com.funmeet.infra.mail;

import com.funmeet.modules.notice.form.MessageForm;
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
@Component
@Slf4j
@RequiredArgsConstructor
public class FunMeetSendStrategy implements SendStrategy {

    private final JavaMailSender javaMailSender;

    @Async
    @Override
    public void sendNotice(MessageForm messageForm) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(messageForm.getTo());
            mimeMessageHelper.setSubject(messageForm.getSubject());
            mimeMessageHelper.setText(messageForm.getText(), true);
            javaMailSender.send(mimeMessage);

            log.info("success send to email : {}", messageForm.getTo());
        } catch (MessagingException e) {
            log.info("fail send to email : {}", messageForm.getTo());
            throw new RuntimeException(e);
        }
    }

    @Override
    public StrategyName getStrategyName() {
        return StrategyName.EMAIL;
    }
}
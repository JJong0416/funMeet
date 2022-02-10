package com.funmeet.infra.mail;


import com.funmeet.modules.notice.form.MessageForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({"local", "junit"})
@Component
@Slf4j
public class ConsoleSendStrategy implements SendStrategy {

    @Override
    public void sendNotice(MessageForm messageForm) {
        log.info("send message to : {}, Text : {}", messageForm.getTo(), messageForm.getText());
    }
}



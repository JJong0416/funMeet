package com.funmeet.mail;


import com.funmeet.form.EmailMessageForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
@Slf4j
public class ConsoleEmailService implements EmailService{

    @Override
    public void send(EmailMessageForm emailMessageForm){
        log.info("email totally : {}",emailMessageForm.getText());
    }
}

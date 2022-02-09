package com.funmeet.infra.mail;

import com.funmeet.infra.mail.form.EmailMessageForm;
import org.springframework.stereotype.Service;

@Service
public class ForwardNoticeService {
    public void sendNoticeWithStrategy(SendStrategy sendStrategy, EmailMessageForm emailMessageForm) {
        sendStrategy.sendNotice(emailMessageForm);
    }
}

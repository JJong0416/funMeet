package com.funmeet.infra.mail;

import com.funmeet.modules.notice.form.MessageForm;
import org.springframework.stereotype.Service;

@Service
public class ConveyStrategyService {
    public void sendNoticeWithStrategy(SendStrategy sendStrategy, MessageForm messageForm) {
        sendStrategy.sendNotice(messageForm);
    }
}

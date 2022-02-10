package com.funmeet.infra.mail;

import com.funmeet.modules.notice.form.MessageForm;

public interface SendStrategy {
    void sendNotice(MessageForm messageForm);

    StrategyName getStrategyName();
}

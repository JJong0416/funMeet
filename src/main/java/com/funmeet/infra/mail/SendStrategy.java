package com.funmeet.infra.mail;

import com.funmeet.infra.mail.form.EmailMessageForm;

public interface SendStrategy {
    void sendNotice(EmailMessageForm emailMessageForm);
}

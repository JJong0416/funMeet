package com.funmeet.mail;

import com.funmeet.form.EmailMessageForm;

public interface EmailService {

    void send(EmailMessageForm emailMessageForm);
}

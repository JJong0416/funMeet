package com.funmeet.modules.notice.form;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Builder
public class MessageForm {
    private String to;

    private String subject;

    private String text;

    public MessageForm(String to, String subject, String text){
        this.to = to;
        this.subject = subject;
        this.text = text;
    }
}

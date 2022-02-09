package com.funmeet.infra.mail.form;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailMessageForm {
    private String to;

    private String subject;

    private String text;
}

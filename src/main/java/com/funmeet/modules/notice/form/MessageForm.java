package com.funmeet.modules.notice.form;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageForm {
    private String to;

    private String subject;

    private String text;
}

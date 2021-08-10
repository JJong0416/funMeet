package com.funmeet.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailMessageForm {

    private String to;

    private String subject;

    private String text;

}

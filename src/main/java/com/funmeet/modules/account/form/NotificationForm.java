package com.funmeet.modules.account.form;

import lombok.Data;

@Data
public class NotificationForm {
    private boolean meetCreatedByEmail;

    private boolean meetCreatedByWeb;

    private boolean meetEnrollmentResultByEmail;

    private boolean meetEnrollmentResultByWeb;

    private boolean meetUpdateByEmail;

    private boolean meetUpdatedByWeb;
}

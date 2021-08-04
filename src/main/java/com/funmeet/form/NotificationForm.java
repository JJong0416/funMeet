package com.funmeet.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class NotificationForm {
    private boolean meetCreatedByEmail;

    private boolean meetCreatedByWeb;

    private boolean meetEnrollmentResultByEmail;

    private boolean meetEnrollmentResultByWeb;

    private boolean meetUpdateByEmail;

    private boolean meetUpdatedByWeb;
}

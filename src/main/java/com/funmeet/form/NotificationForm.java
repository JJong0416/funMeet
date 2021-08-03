package com.funmeet.form;

import com.funmeet.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationForm {
    private boolean meetCreatedByEmail;

    private boolean meetCreatedByWeb;

    private boolean meetEnrollmentResultByEmail;

    private boolean meetEnrollmentResultByWeb;

    private boolean meetUpdateByEmail;

    private boolean meetUpdatedByWeb;

    public NotificationForm(Account account){
        this.meetCreatedByEmail = account.isMeetCreatedByEmail();
        this.meetCreatedByWeb = account.isMeetCreatedByWeb();
        this.meetEnrollmentResultByEmail = account.isMeetEnrollmentResultByEmail();
        this.meetEnrollmentResultByWeb = account.isMeetEnrollmentResultByWeb();
        this.meetUpdateByEmail = account.isMeetUpdateByEmail();
        this.meetUpdatedByWeb = account.isMeetUpdatedByWeb();
    }

}

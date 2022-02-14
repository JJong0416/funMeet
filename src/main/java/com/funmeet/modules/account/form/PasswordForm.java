package com.funmeet.modules.account.form;

import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter @NoArgsConstructor @AllArgsConstructor @Setter
public class PasswordForm {

    @Length(min = 6, max = 30)
    private String newPassword;

    @Length(min = 6, max = 30)
    private String newPasswordConfirm;
}

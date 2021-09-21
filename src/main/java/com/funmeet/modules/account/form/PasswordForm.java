package com.funmeet.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {

    @Length(min = 6, max = 30)
    private String newPassword;

    @Length(min = 6, max = 30)
    private String newPasswordConfirm;
}

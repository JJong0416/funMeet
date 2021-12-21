package com.funmeet.modules.account.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class PasswordForm {

    @Length(min = 6, max = 30)
    private String newPassword;

    @Length(min = 6, max = 30)
    private String newPasswordConfirm;
}

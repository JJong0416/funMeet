package com.funmeet.modules.account.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @NoArgsConstructor
@AllArgsConstructor @Setter
public class SignUpForm {

    @NotBlank
    @Length(min=6, max=20)
    @Pattern(regexp="^[ㄱ-ㅎ가-힣a-zA-Z0-9_-]{6,20}$")
    private String nickname;

    @NotBlank
    @Length(min = 6, max = 20)
    private String password;

    @Email
    @NotBlank
    private String email;
}

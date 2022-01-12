package com.funmeet.modules.account.form;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @NoArgsConstructor
@AllArgsConstructor @Setter @Builder
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

    private String shortBio = "간략한 자기소개를 추가하세요";
}

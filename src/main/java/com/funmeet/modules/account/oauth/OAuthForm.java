package com.funmeet.modules.account.oauth;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class OAuthForm {

    @NotBlank
    @Length(min=6, max=20)
    @Pattern(regexp="^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;

    @NotBlank
    @Length(min = 6, max = 20)
    private String password;
}

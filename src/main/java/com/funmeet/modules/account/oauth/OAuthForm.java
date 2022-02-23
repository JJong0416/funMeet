package com.funmeet.modules.account.oauth;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter @Builder
public class OAuthForm {

    @NotBlank
    @Length(min=6, max=20)
    @Pattern(regexp="^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;

    @NotBlank
    @Length(min = 6, max = 20)
    private String password;

    @Email
    @NotBlank
    private String email;

    private final String shortBio = "간략한 자기소개를 추가하세요";
}

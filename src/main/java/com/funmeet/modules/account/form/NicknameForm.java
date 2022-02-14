package com.funmeet.modules.account.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @NoArgsConstructor
public class NicknameForm {

    @NotBlank
    @Length(min = 6, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9_-]{6,20}$")
    private String nickname;

    public NicknameForm(String nickname){
        this.nickname = nickname;
    }
}

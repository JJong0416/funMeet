package com.funmeet.modules.club.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class ClubForm {

    @NotBlank
    @Length(min = 2, max = 15)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-]{2,15}$")
    private String clubPath;

    @NotBlank
    @Length(max = 20)
    private String title;


    @NotBlank
    @Length(max = 25)
    private String shortDescription;

    @NotBlank
    private String fullDescription;
}
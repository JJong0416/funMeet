package com.funmeet.modules.club.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ClubForm {

    @NotBlank
    @Length(min = 2, max = 15)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,15}$")
    private String clubPath;

    @NotBlank
    @Length(max = 20)
    private String title;


    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;

}
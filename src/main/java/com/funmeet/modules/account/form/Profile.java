package com.funmeet.modules.account.form;

import com.funmeet.modules.account.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class Profile {

    private String nickname;

    @Length(min=1,max=25)
    private String short_bio;

    @Length(max=10)
    private String occupation;

    private String profileImage;

    public Profile(Account account){
        this.nickname = account.getNickname();
        this.short_bio = account.getShort_bio();
        this.occupation = account.getOccupation();
        this.profileImage = account.getProfileImage();
    }
}

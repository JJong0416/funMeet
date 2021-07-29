package com.funmeet.form;

import com.funmeet.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Profile {

    private String nickname;

    private String short_bio;

    private String occupation;

    public Profile(Account account){
        this.nickname = account.getNickname();
        this.short_bio = account.getShort_bio();
        this.occupation = account.getOccupation();
    }
}

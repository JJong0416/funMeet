package com.funmeet.form;

import com.funmeet.domain.Account;
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

    public Profile(Account account){
        this.nickname = account.getNickname();
        this.short_bio = account.getShort_bio();
        this.occupation = account.getOccupation();
    }
}

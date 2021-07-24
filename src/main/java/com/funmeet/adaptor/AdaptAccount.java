package com.funmeet.adaptor;

import com.funmeet.domain.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class AdaptAccount extends User {

    private final Account account;

    public AdaptAccount(Account account){
        super(account.getNickname(),account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}
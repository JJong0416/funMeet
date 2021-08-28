package com.funmeet.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    @Autowired
    AccountRepository accountRepository;

    public Account createNewAccount(String nickname) {
        Account jongchan = new Account();
        jongchan.setEmail(nickname + "@gmail.com");
        jongchan.setNickname(nickname);
        accountRepository.save(jongchan);
        return jongchan;
    }

}
package com.funmeet.modules.account;

import com.funmeet.infra.mail.ConveyStrategyService;
import com.funmeet.infra.mail.SendStrategy;
import com.funmeet.modules.notice.NoticeFactory;
import com.funmeet.modules.notice.form.MessageForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountEmailService {

    private final SendStrategy sendStrategy;
    private final AccountService accountService;
    private final NoticeFactory noticeFactory;

    public void sendSignUpConfirmEmail(String email) {
        Account account = accountService.findAccountByEmail(email);
        MessageForm messageForm = noticeFactory.makeAccountNoticeForm(account,
                "/check-email-token?token=",
                "이메일 인증하기");
        sendStrategy.sendNotice(messageForm);
    }

    public void sendLoginLink(String email) {
        Account account = accountService.findAccountByEmail(email);
        MessageForm messageForm = noticeFactory.makeAccountNoticeForm(account,
                "/auth-email?token=",
                "로그인하기");
        sendStrategy.sendNotice(messageForm);
    }
}

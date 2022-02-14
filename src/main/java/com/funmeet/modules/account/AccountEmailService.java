package com.funmeet.modules.account;

import com.funmeet.infra.mail.StrategyFactory;
import com.funmeet.modules.notice.NoticeFactory;
import com.funmeet.modules.notice.form.MessageForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountEmailService {

    private final AccountService accountService;
    private final NoticeFactory noticeFactory;

    public void sendSignUpConfirmEmail(String email) {
        Account account = accountService.findAccountByEmail(email);
        MessageForm messageForm = noticeFactory.makeAccountNoticeForm(account,
                "/check-email-token?token=",
                "이메일 인증하기");
        noticeFactory.sendEmail(messageForm);
    }

    public void sendLoginLink(String email) {
        Account account = accountService.findAccountByEmail(email);
        MessageForm messageForm = noticeFactory.makeAccountNoticeForm(account,
                "/auth-email?token=",
                "로그인하기");
        noticeFactory.sendEmail(messageForm);
    }
}

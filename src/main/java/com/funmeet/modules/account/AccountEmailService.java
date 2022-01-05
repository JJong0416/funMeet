package com.funmeet.modules.account;

import com.funmeet.infra.config.AppProperties;
import com.funmeet.infra.mail.EmailMessageForm;
import com.funmeet.infra.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor


public class AccountEmailService {

    // TODO: 2022-01-04 공통 서비스로 만들기

    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final EmailService emailService;
    private final AccountService accountService;

    public EmailMessageForm writeEmailMessage(Account account, String link, String linkName){
        Context context = new Context();
        context.setVariable("link",link + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("nickname",account.getNickname());
        context.setVariable("message","뻔모임 서비스를 이용하시려면 링크를 클릭하세요.");
        context.setVariable("linkName",linkName);
        context.setVariable("host",appProperties.getHost());

        String message = templateEngine.process("email/html-email-link",context);

        return EmailMessageForm.builder()
                .to(account.getEmail())
                .subject("뻔(Fun)하면서 뻔하지 않은 모임. 뻔모임 로그인 링크")
                .text(message)
                .build();
    }

    // 써야 하는 것은 link, nickname, host
    public void sendSignUpConfirmEmail(String email) {
        Account account = accountService.findAccountByEmail(email);
        EmailMessageForm emailMessageForm = writeEmailMessage(account,
                "/check-email-token?token=",
                "이메일 인증하기");
        emailService.send(emailMessageForm);
    }

    public void sendLoginLink(String email) {
        Account account = accountService.findAccountByEmail(email);
        EmailMessageForm emailMessageForm = writeEmailMessage(account,
                "/auth-email?token=",
                "로그인하기");
        emailService.send(emailMessageForm);
    }
}

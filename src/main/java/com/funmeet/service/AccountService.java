package com.funmeet.service;

import com.funmeet.adaptor.AdaptAccount;
import com.funmeet.domain.Account;
import com.funmeet.form.SignUpForm;
import com.funmeet.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final AccountRepository accountRepository;

    @Transactional
    public Account processSignUpAccount(SignUpForm signUpForm) {
        Account newAccount = saveSignUpAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveSignUpAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .nickname(signUpForm.getNickname())
                .email(signUpForm.getEmail())
                .password(passwordEncoder.encode(signUpForm.getPassword())) // password Encording 해야한다.
                .meetCreatedByWeb(true)
                .meetEnrollmentResultByWeb(true)
                .meetUpdatedByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("뻔(Fun)하면서 뻔하지 않은 모임. 뻔모임 회원가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new AdaptAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("RULE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}

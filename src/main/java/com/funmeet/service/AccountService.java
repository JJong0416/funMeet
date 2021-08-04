package com.funmeet.service;

import com.funmeet.adaptor.AdaptAccount;
import com.funmeet.domain.Account;
import com.funmeet.form.NotificationForm;
import com.funmeet.form.Profile;
import com.funmeet.form.SignUpForm;
import com.funmeet.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;


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
                .short_bio("간략한 자기 소개를 추가하세요.")
                .meetCreatedByWeb(true)
                .meetEnrollmentResultByWeb(true)
                .meetUpdatedByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("뻔(Fun)하면서 뻔하지 않은 모임. 뻔모임 회원가입 인증");
        mailMessage.setText("/check_email_token?token=" + newAccount.getEmailCheckToken() +
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

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String EmailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(EmailOrNickname);
        if (account == null){
            account = accountRepository.findByNickname(EmailOrNickname);
        }

        if (account == null){
            throw new UsernameNotFoundException(EmailOrNickname);
        }
        return new AdaptAccount(account);
    }

    public void updateProfile(Account account, Profile profile) {
        account.setShort_bio(profile.getShort_bio());
        account.setProfileImage(profile.getProfileImage());
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotification(Account account, NotificationForm notificationForm){

        modelMapper.map(notificationForm,account);
        accountRepository.save(account);
    }

    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }
}


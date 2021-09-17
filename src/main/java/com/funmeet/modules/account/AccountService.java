package com.funmeet.modules.account;

import com.funmeet.infra.config.AppProperties;
import com.funmeet.infra.mail.EmailMessageForm;
import com.funmeet.infra.mail.EmailService;
import com.funmeet.modules.account.form.NotificationForm;
import com.funmeet.modules.account.form.Profile;
import com.funmeet.modules.account.form.SignUpForm;
import com.funmeet.modules.account.oauth.OAuthForm;
import com.funmeet.modules.city.City;
import com.funmeet.modules.hobby.Hobby;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public Account processSignUpAccount(SignUpForm signUpForm) {
        Account newAccount = saveSignUpAccount(signUpForm);
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveSignUpAccount(SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm,Account.class);
        account.generateEmailCheckToken();;
        account.setShort_bio("간략한 자기 소개를 추가하세요.");
        return accountRepository.save(account);
    }

    public Account oauthSignUp(OAuthForm oAuthForm, String kakaoEmail) {
        Account account = modelMapper.map(oAuthForm,Account.class);
        account.setKakaoTokenVerified(true);
        this.completeSignUp(account);
        account.setKakaoEmail(kakaoEmail);
        account.setShort_bio("간략한 자기 소개를 추가하세요.");

        return accountRepository.save(account);
    }

    // 써야 하는 것은 link, nickname, host
    public void sendSignUpConfirmEmail(Account addAccount) {
        Context context = new Context();
        context.setVariable("link","/check_email_token?token=" + addAccount.getEmailCheckToken() +
                "&email=" + addAccount.getEmail());
        context.setVariable("nickname",addAccount.getNickname());
        context.setVariable("message","뻔모임 서비스를 이용하시려면 링크를 클릭하세요.");
        context.setVariable("linkName","이메일 인증하기");
        context.setVariable("host",appProperties.getHost());

        String message = templateEngine.process("email/html_email_link",context);

        EmailMessageForm emailMessageForm = EmailMessageForm.builder()
                .to(addAccount.getEmail())
                .subject("뻔(Fun)하면서 뻔하지 않은 모임. 뻔모임 회원가입 인증")
                .text(message)
                .build();

        emailService.send(emailMessageForm);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void sendLoginLink(Account account) {
        Context context = new Context();
        context.setVariable("link","/auth_email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("nickname",account.getNickname());
        context.setVariable("message","뻔모임 서비스를 이용하시려면 링크를 클릭하세요.");
        context.setVariable("linkName","로그인하기");
        context.setVariable("host",appProperties.getHost());

        String message = templateEngine.process("email/html_email_link",context);

        EmailMessageForm emailMessageForm = EmailMessageForm.builder()
                .to(account.getEmail())
                .subject("뻔(Fun)하면서 뻔하지 않은 모임. 뻔모임 로그인 링크")
                .text(message)
                .build();
        emailService.send(emailMessageForm);

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
        if (account != null){
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

    public List<Hobby> getHobby(Account account){
        Optional<Account> getId = accountRepository.findById(account.getId());
        return getId.orElseThrow().getHobby();
    }

    public void addHobby(Account account, Hobby hobby){
        Optional<Account> user = accountRepository.findById(account.getId());
        user.ifPresent(a -> a.getHobby().add(hobby));
    }

    public void removeHobby(Account account, Hobby hobby) {
        Optional<Account> removeId = accountRepository.findById(account.getId());
        removeId.ifPresent(a -> a.getHobby().remove(hobby));
    }

    public List<City> getCity(Account account) {
        Optional<Account> getId = accountRepository.findById(account.getId());
        return getId.orElseThrow().getCity();
    }

    public void addCity(Account account, City city) {
        Optional<Account> getId = accountRepository.findById(account.getId());
        getId.ifPresent(a -> a.getCity().add(city));
    }

    public void removeCity(Account account, City city) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getCity().remove(city));
    }

    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }

    public void addOauthAccount(){

    }


}


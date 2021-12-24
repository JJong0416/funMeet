package com.funmeet.modules.account;

import com.funmeet.infra.config.AppProperties;
import com.funmeet.infra.mail.EmailMessageForm;
import com.funmeet.infra.mail.EmailService;
import com.funmeet.modules.account.form.NotificationForm;
import com.funmeet.modules.account.form.Profile;
import com.funmeet.modules.account.form.SignUpForm;
import com.funmeet.modules.account.oauth.OAuthForm;
import com.funmeet.modules.account.security.AdaptAccount;
import com.funmeet.modules.city.City;
import com.funmeet.modules.city.CityRepository;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyRepository;
import com.funmeet.modules.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final CityRepository cityRepository;
    private final HobbyRepository hobbyRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public Account processSignUpAccount(SignUpForm signUpForm) {
        Account newAccount = saveSignUp(signUpForm);
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveSignUp(SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = AccountMapper.INSTANCE.signUpFormToEntity(signUpForm);
        account.generateEmailCheckToken();
        return accountRepository.save(account);
    }

    public Account saveOauthSignUp(OAuthForm oAuthForm, String kakaoEmail) {
        oAuthForm.setPassword(passwordEncoder.encode(oAuthForm.getPassword()));
        Account account = AccountMapper.INSTANCE.oauthFormToEntity(oAuthForm);
        return accountRepository.save(account);
    }

    // 써야 하는 것은 link, nickname, host
    public void sendSignUpConfirmEmail(Account account) {
        EmailMessageForm emailMessageForm = writeEmailMessage(account, "/check-email-token?token=", "이메일 인증하기");
        emailService.send(emailMessageForm);
    }

    public void sendLoginLink(Account account) {
        EmailMessageForm emailMessageForm = writeEmailMessage(account, "/auth-email?token=", "로그인하기");
        emailService.send(emailMessageForm);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new AdaptAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String findAccount) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(findAccount);
        if (account == null){
            account = accountRepository.findByNickname(findAccount);
        }

        if (account == null){
            throw new UsernameNotFoundException(findAccount);
        }
        return new AdaptAccount(account);
    }

    public Account findAccountByEmail(String email){
        return accountRepository.findByEmail(email);
    }

    public Account findAccountByNickname(String nickname){
        return accountRepository.findByNickname(nickname);
    }

    public void updateProfile(Account account, Profile profile) {
        account.completeProfile(profile.getShortBio(), profile.getProfileImage());
    }

    public void updatePassword(Account account, String newPassword) {
        account.updatePassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotification(Account account, NotificationForm notificationForm){
        account.updateNotification(notificationForm);
        accountRepository.save(account);
    }

    public void updateNickname(Account account, String nickname) {
        account.updateNickname(nickname);
        accountRepository.save(account);
        login(account);
    }

    public Hobby findHobbyByTitle(String title){
        return hobbyRepository.findByTitle(title).orElseThrow();
    }

    public Set<Hobby> getHobby(Account account){
        Optional<Account> getId = accountRepository.findById(account.getId());
        return getId.orElseThrow().getHobby();
    }

    public List<String> getAllHobby(Account account){
        return hobbyRepository.findAll().stream().map(Hobby::getTitle).collect(Collectors.toList());
    }

    public void addHobby(Account account, Hobby hobby){
        Optional<Account> user = accountRepository.findById(account.getId());
        user.ifPresent(a -> a.getHobby().add(hobby));
    }

    public void removeHobby(Account account, Hobby hobby) {
        Optional<Account> removeId = accountRepository.findById(account.getId());
        removeId.ifPresent(a -> a.getHobby().remove(hobby));
    }

    public City findCityByKrCity(String koCity){
        return cityRepository.findByKrCity(koCity);
    }

    public Set<City> getCity(Account account) {
        Optional<Account> getId = accountRepository.findById(account.getId());
        return getId.orElseThrow().getCity();
    }

    public List<String> getAllCity(Account account){
        return cityRepository.findAll().stream().map(City::toString).collect(Collectors.toList());
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

    /* Method 분할 */

    private EmailMessageForm writeEmailMessage(Account account, String link, String linkName){
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

}
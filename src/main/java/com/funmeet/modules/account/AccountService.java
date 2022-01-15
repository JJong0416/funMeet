package com.funmeet.modules.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funmeet.modules.account.form.NotificationForm;
import com.funmeet.modules.account.form.Profile;
import com.funmeet.modules.account.form.SignUpForm;
import com.funmeet.modules.account.oauth.OAuthForm;
import com.funmeet.modules.city.City;
import com.funmeet.modules.city.CityRepository;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyRepository;
import com.funmeet.modules.hobby.HobbyService;
import com.funmeet.modules.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService{

    private final AccountRepository accountRepository;
    private final CityRepository cityRepository;
    private final HobbyRepository hobbyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountDetailsService accountDetailsService;
    private final HobbyService hobbyService;
    private final ObjectMapper objectMapper;


    public void processSignUpAccount(SignUpForm signUpForm) {
        Account newAccount = saveSignUp(signUpForm);
        accountDetailsService.loginByAccount(newAccount);
    }

    public boolean isValidToken(String email, String token){
        Account account = this.findAccountByEmail(email);
        return account.isValidToken(token);
    }


    public Account saveOauthSignUp(OAuthForm oAuthForm, String kakaoEmail) {
        oAuthForm.setPassword(passwordEncoder.encode(oAuthForm.getPassword()));
        Account account = AccountMapper.INSTANCE.oauthFormToEntity(oAuthForm);
        return accountRepository.save(account);
    }

    public Account completeSignUp(String email) {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
            throw new UsernameNotFoundException(email);
        });

        account.completeSignUp();
        accountDetailsService.loginByAccount(account);
        return account;
    }

    public Account findAccountByEmail(String email){
        return accountRepository.findByEmail(email).orElseThrow(() -> {
            throw new UsernameNotFoundException(email);
        });
    }

    public boolean canSendConfirmEmail(String email){
        Account account = this.findAccountByEmail(email);
        return account.canSendConfirmEmail();
    }

    public Account findAccountByNickname(String nickname){
        return accountRepository.findByNickname(nickname).orElseThrow( () -> {
            throw new UsernameNotFoundException(nickname);
        });
    }

    public List<String> getHobby(Account account){
        Optional<Account> getId = accountRepository.findById(account.getId());
        return getId.orElseThrow().getHobby().stream().map(Hobby::getTitle).collect(Collectors.toList());
    }

    public String getWhiteListHobby(Account account) throws JsonProcessingException {
        List<String> whiteList = hobbyRepository.findAll().stream().map(Hobby::getTitle).collect(Collectors.toList());
        return objectMapper.writeValueAsString(whiteList);
    }

    public void addHobby(Account account, String hobbyName){
        Hobby hobby = hobbyService.findOrCreateHobby(hobbyName);
        Optional<Account> user = accountRepository.findById(account.getId());
        user.ifPresent(a -> a.getHobby().add(hobby));
    }

    public boolean removeHobby(Account account, String hobbyName) {
        Hobby hobby = hobbyRepository.findByTitle(hobbyName).orElseThrow();
        Optional<Account> removeId = accountRepository.findById(account.getId());
        removeId.ifPresent(a -> a.getHobby().remove(hobby));
        return removeId.isPresent();
    }

    public List<String> getCity(Account account) {
        Optional<Account> getId = accountRepository.findById(account.getId());
        return getId.orElseThrow().getCity().stream().map(City::toString).collect(Collectors.toList());
    }

    public String getWhiteListCity(Account account) throws JsonProcessingException {
        List<String> whiteList = cityRepository.findAll().stream().map(City::toString).collect(Collectors.toList());
        return objectMapper.writeValueAsString(whiteList);
    }

    public City findCityByKrCity(String koCity){
        return cityRepository.findByKrCity(koCity).orElseThrow();
    }

    public void addCity(Account account, String cityName) {
        City city = this.findCityByKrCity(cityName);
        Optional<Account> getId = accountRepository.findById(account.getId());
        getId.ifPresent(a -> a.getCity().add(city));
    }

    public boolean removeCity(Account account, String cityName) {
        City city = this.findCityByKrCity(cityName);
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getCity().remove(city));
        return byId.isPresent();
    }

    public void updateProfile(Account account, Profile profile) {
        account.completeProfile(profile.getShortBio(), profile.getProfileImage());
        accountRepository.save(account);
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
        accountDetailsService.loginByAccount(account);
    }

    public void deleteAccount(Account account, HttpServletRequest request) {
        accountRepository.removeAllHobbyAndCityByEmail(account.getEmail());
        accountRepository.delete(account);
        accountDetailsService.logout(request);
    }

    private Account saveSignUp(SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = AccountMapper.INSTANCE.signUpFormToEntity(signUpForm);
        account.generateEmailCheckToken();
        return accountRepository.save(account);
    }
}
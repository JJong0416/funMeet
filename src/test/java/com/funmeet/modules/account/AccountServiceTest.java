package com.funmeet.modules.account;

import com.funmeet.modules.account.form.SignUpForm;
import com.funmeet.modules.account.oauth.OAuthForm;
import com.funmeet.modules.hobby.Hobby;
import com.funmeet.modules.hobby.HobbyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    AccountService accountService;

    @Mock
    AccountRepository accountRepository;

    @Mock
    HobbyService hobbyService;

    @Mock
    AccountDetailsService accountDetailsService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 절차 - 성공")
    void 정상적인_사용자_데이터가_들어왔을때_회원_가입을_성공한다() {
        // given
        final SignUpForm signUpForm = AccountFactory.createSignupForm();
        final Account account = Account.builder()
                .nickname("account001")
                .email("test@test.com")
                .password("password001")
                .build();

        when(passwordEncoder.encode(any())).thenReturn("encodePassword");
        when(accountRepository.findByEmail("test@test.com")).thenReturn(Optional.of(account));
        willDoNothing().given(accountDetailsService).loginByAccount(any());

        // when
        accountService.processSignUpAccount(signUpForm);

        // then
        assertThat(signUpForm.getPassword()).isEqualTo("encodePassword");
        assertThat(accountRepository.findByEmail("test@test.com")).isEqualTo(Optional.of(account));
    }

    @Test
    @DisplayName("Oauth(카카오톡) 회원가입 - 성공")
    void 정상적인_카카오톡_사용자_데이터가_들어왔을때_회원_가입을_성공한다() {
        // given
        final Account account = AccountFactory.createSuccessAccount();
        final OAuthForm oAuthForm = OAuthForm.builder()
                .nickname("account001")
                .password("password001")
                .email("test@test.com")
                .build();

        when(passwordEncoder.encode(any())).thenReturn("encodePassword");
        when(accountRepository.save(any())).thenReturn(account);

        // when
        Account newAccount = accountService.saveOauthSignUp(oAuthForm, "");

        // then
        assertThat(oAuthForm.getPassword()).isEqualTo("encodePassword");
        assertThat(newAccount).isEqualTo(account);
    }

    @Test
    @DisplayName("이메일을 통해 계정 찾기 - 성공")
    void 이미_기존의_계정을_이메일을_통해_찾으려고_할때_기존계정을_가지고온다() {
        final Account account = AccountFactory.createSuccessAccount();
        when(accountRepository.findByEmail("test@test.com")).thenReturn(Optional.of(account));

        var findAccount = accountService.findAccountByEmail("test@test.com");

        assertThat(findAccount).isEqualTo(account);
    }

    @Test
    @DisplayName("이메일을 통해 계정 찾기 - 실패")
    void 기존에_없는_계정을_이메일을_통해_찾으려고_할때_Exception을_터트린다() {
        final String USER_EMAIL = "test@test.com";
        when(accountRepository.findByEmail(USER_EMAIL)).thenThrow(new UsernameNotFoundException(USER_EMAIL));

        assertThatThrownBy(() -> accountService.findAccountByEmail(USER_EMAIL))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(USER_EMAIL);
    }

    @Test
    @DisplayName("닉네임을 통해 계정 찾기 - 성공")
    void 이미_기존의_계정을_닉네임을_통해_찾으려고_할때_기존계정을_가지고온다() {
        final Account account = AccountFactory.createSuccessAccount();
        final String NICKNAME = "account001";
        when(accountRepository.findByNickname(NICKNAME)).thenReturn(Optional.of(account));

        var findAccount = accountService.findAccountByNickname(NICKNAME);

        assertThat(findAccount).isEqualTo(account);
    }

    @Test
    @DisplayName("닉네임을 통해 계정 찾기 - 실패")
    void 기존에_없는_계정을_닉네임을_통해_찾으려고_할때_Exception을_터트린다() {
        final String NICKNAME = "account001";
        when(accountRepository.findByNickname(NICKNAME)).thenThrow(new UsernameNotFoundException(NICKNAME));

        assertThatThrownBy(() -> accountService.findAccountByNickname(NICKNAME))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(NICKNAME);
    }

    @Test
    @DisplayName("계정에 등록한 취미들을 가지고 오기 - 성공")
    void 사용자가_등록한_취미들을_모두다_가지고_온다() {
        final Account account = AccountFactory.createSuccessAccount();
        final String HOBBY_NAME = "스프링";
        final Hobby hobby = new Hobby(0L, HOBBY_NAME);
        account.getHobby().add(hobby);
        when(accountRepository.findById(any())).thenReturn(Optional.of(account));

        List<String> hobbies = accountService.getHobby(account);

        assertThat(hobbies.size()).isEqualTo(1);
        assertThat(hobbies.get(0)).isEqualTo(HOBBY_NAME);
    }

    @Test
    @DisplayName("계정에 취미를 등록하기 - 성공")
    void 사용자가_올바른_취미들을_등록한다() {
        final Account account = AccountFactory.createSuccessAccount();
        final String HOBBY_NAME = "스프링";
        final Hobby hobby = new Hobby(0L, HOBBY_NAME);
        when(hobbyService.findOrCreateHobby(HOBBY_NAME)).thenReturn(hobby);
        when(accountRepository.findById(any())).thenReturn(Optional.of(account));

        accountService.addHobby(account, HOBBY_NAME);

        assertThat(account.getHobby()).size().isEqualTo(1);
        assertThat(account.getHobby()).contains(hobby);
    }
}
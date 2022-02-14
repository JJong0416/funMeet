package com.funmeet.modules.account;

import com.funmeet.modules.account.form.SignUpForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
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
    AccountDetailsService accountDetailsService;

    @Mock
    PasswordEncoder passwordEncoder;


    @Test
    void 정상적인_사용자_데이터가_들어왔을때_성공적으로_회원_가입을_한다() {

        final SignUpForm signUpForm = SignUpForm.builder()
                .nickname("testAccount")
                .password("testPassword")
                .email("test@test.com")
                .shortBio("간략한 자기소개를 추가하세요")
                .build();

        final Account account = Account.builder()
                .nickname("testAccount")
                .password("testPassword")
                .email("test@test.com")
                .shortBio("간략한 자기소개를 추가하세요")
                .build();

        when(accountRepository.save(account)).thenReturn(account);
        assertThat(accountService.saveSignUp(signUpForm)).isEqualTo(account);
    }
}

//given
//        final SignUpForm signUpForm = SignUpForm.builder()
//                .nickname("testAccount")
//                .password("testPassword")
//                .email("test@test.com")
//                .shortBio("간략한 자기소개를 추가하세요")
//                .build();
//
//        final Account account = Account.builder()
//                .nickname("testAccount")
//                .password("testPassword")
//                .email("test@test.com")
//                .shortBio("간략한 자기소개를 추가하세요")
//                .build();
//
//
//        when(accountRepository.save(any())).thenReturn(account);
//        willDoNothing().given(accountDetailsService).loginByAccount(any());
//
//        //when
//        Account test_account = accountService.saveSignUp(signUpForm);
//
//        //then
//        assertThat(test_account).isEqualTo(account);
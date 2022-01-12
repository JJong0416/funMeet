package com.funmeet.modules.account;

import com.funmeet.modules.account.form.SignUpForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock AccountRepository accountRepository;

    @Mock AccountDetailsService accountDetailsService;

    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks AccountService accountService;

    @Test
    void 정상적인_사용자_데이터가_들어왔을때_성공적으로_회원_가입을_한다(){
        //given
        final SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("testAccount");
        signUpForm.setPassword("testPassword");
        signUpForm.setEmail("test@test.com");
        signUpForm.setShortBio("간략한 자기소개를 추가하세요");

        final Account account = Account.builder()
                .nickname("testAccount")
                .password("encodedPassword")
                .email("test@test.com")
                .shortBio("간략한 자기소개를 추가하세요")
                .build();

        willReturn("encodedPassword").given(passwordEncoder).encode(any());
        willReturn(account).given(accountRepository).save(any());
        willDoNothing().given(accountDetailsService).loginByAccount(any());

        //when
        accountService.processSignUpAccount(signUpForm);

        //then

    }


}

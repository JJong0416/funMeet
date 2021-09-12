package com.funmeet.modules.account;

import com.funmeet.infra.MockMvcTest;
import com.funmeet.infra.mail.EmailMessageForm;
import com.funmeet.infra.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @MockBean
    EmailService emailService;

    @DisplayName("회원 가입 화면 보이는지")
    @Test
    void signUpForm() {
        try {
            mockMvc.perform(get("/sign_up"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("account/sign_up"))
                    .andExpect(model().attributeExists("signUpForm"))
                    .andExpect(unauthenticated());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("회원가입 - 잘못된 입력값")
    @Test
    void signUpSubmit_wrong_input() throws Exception{
        mockMvc.perform(post("/sign_up")
                .param("nickname","jjong")
                .param("email","checkemal....@")
                .param("password","123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
                .andExpect(view().name("account/sign_up"));
    }

    @DisplayName("회원가입 - 정상적인 입력값")
    @Test
    void signUpSubmit_right_input() throws Exception{
        mockMvc.perform(post("/sign_up")
                .param("nickname","jjong0416")
                .param("email","checkemal@naver.com")
                .param("password","assddsssaa")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("jjong0416"));

        Account account = accountRepository.findByNickname("jjong0416");
        assertNotNull(account);

        assertNotEquals(account.getPassword(),"assddsssaa");

        assertTrue(accountRepository.existsByNickname("jjong0416"));
        assertTrue(accountRepository.existsByEmail("checkemal@naver.com"));
        then(emailService).should().send(any(EmailMessageForm.class)); // 메일을 보냈는지 체크해주는 것
    }

    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    void checked_Email_wrong_input() throws Exception{
        mockMvc.perform(get("/check_email_token")
                .param("token","sgdspsf")
                .param("email","email@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("email/check_email"))
                .andExpect(unauthenticated());
    }

    @DisplayName("인증 메일 확인 - 입력값 정상 처리")
    @Test
    void checked_Email_right_input() throws Exception{
        Account account = Account.builder()
                .email("test@gmail.com")
                .password("12asd456")
                .nickname("hello")
                .build();

        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check_email_token")
                .param("token",newAccount.getEmailCheckToken())
                .param("email",newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(view().name("email/check_email"))
                .andExpect(authenticated());
    }
}
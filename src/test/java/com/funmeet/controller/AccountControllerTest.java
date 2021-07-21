package com.funmeet.controller;

import com.funmeet.domain.Account;
import com.funmeet.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    void signUpForm() {
        try {
            mockMvc.perform(get("/sign-up"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("account/sign-up"))
                    .andExpect(model().attributeExists("signUpForm"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("회원가입 - 잘못된 입력값")
    @Test
    void signUpSubmit_wrong_input() throws Exception{
        mockMvc.perform(post("/sign-up")
                .param("nickname","jjong")
                .param("email","checkemal....@")
                .param("password","123")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("회원가입 - 정상적인 입력값")
    @Test
    void signUpSubmit_right_input() throws Exception{
        mockMvc.perform(post("/sign-up")
                .param("nickname","jjong0416")
                .param("email","checkemal@naver.com")
                .param("password","assddsssaa")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByNickname("jjong0416");
        assertNotNull(account);

        assertNotEquals(account.getPassword(),"assddsssaa");

        assertTrue(accountRepository.existsByNickname("jjong0416"));
        assertTrue(accountRepository.existsByEmail("checkemal@naver.com"));
        then(javaMailSender).should().send(any(SimpleMailMessage.class)); // 메일을 보냈는지 체크해주는 것
    }

}
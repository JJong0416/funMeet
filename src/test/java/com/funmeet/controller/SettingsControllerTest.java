package com.funmeet.controller;

import com.funmeet.domain.Account;
import com.funmeet.form.SignUpForm;
import com.funmeet.repository.AccountRepository;
import com.funmeet.service.AccountService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired AccountService accountService;

    @Autowired AccountRepository accountRepository;

    @BeforeEach
    void beforeEach(){
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("jongchan");
        signUpForm.setPassword("12345678");
        signUpForm.setEmail("jjong@gmail.com");
        accountService.processSignUpAccount(signUpForm);
    }

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();;
    }

    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile_right() throws Exception{

        String short_bio = "짧은 소개 수정";

        mockMvc.perform(post("/settings/profile")
                .param("short_bio",short_bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attribute("message","성공"));

        Account jongchan = accountRepository.findByNickname("jongchan");
        assertEquals(short_bio,jongchan.getShort_bio());
    }

    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_wrong() throws Exception{

        String short_bio = "길게 소개를 수정하는 경우.길게 소개를 수정하는 경우길게 소개를 수정하는 경우길게 소개를 수정하는 경우길게 소개를 수정하는 경우길게 소개를 수정하는 경우";

        mockMvc.perform(post("/settings/profile")
                .param("short_bio",short_bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account jongchan = accountRepository.findByNickname("jongchan");
        assertEquals("간략한 자기 소개를 추가하세요.",jongchan.getShort_bio());
    }
}
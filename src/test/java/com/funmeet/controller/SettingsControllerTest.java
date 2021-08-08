package com.funmeet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funmeet.domain.Account;
import com.funmeet.domain.Hobby;
import com.funmeet.form.HobbyForm;
import com.funmeet.form.SignUpForm;
import com.funmeet.repository.AccountRepository;
import com.funmeet.repository.HobbyRepository;
import com.funmeet.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc

class SettingsControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired AccountService accountService;

    @Autowired AccountRepository accountRepository;

    @Autowired PasswordEncoder passwordEncoder;

    @Autowired HobbyRepository hobbyRepository;

    @Autowired ObjectMapper objectMapper;

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
    @DisplayName("프로필 수정폼")
    @Test
    void updateProfile() throws Exception{
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
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


    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("보안 폼")
    @Test
    void updatePassword() throws Exception{
        mockMvc.perform(get("/settings/security"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    void updatePassword_right() throws Exception {
        mockMvc.perform(post("/settings/security")
                .param("newPassword", "asasasas")
                .param("newPasswordConfirm", "asasasas")
                .with(csrf()))
                .andExpect(redirectedUrl("/settings/security"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));

        Account jongchan = accountRepository.findByNickname("jongchan");
        assertTrue(passwordEncoder.matches("asasasas", jongchan.getPassword()));
    }


    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("알림 설정 폼")
    @Test
    void updateNotification() throws Exception{
        mockMvc.perform(get("/settings/notification"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notification"));
    }

    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("닉네임 수정 폼")
    @Test
    void updateAccountForm() throws Exception {
        mockMvc.perform(get("/settings/account"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateAccount_success() throws Exception {
        String newNickname = "jongchans";
        mockMvc.perform(post("/settings/account")
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/account"))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname("jongchans"));
    }

    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("닉네임 수정하기 - 입력값 에러")
    @Test
    void updateAccount_failure() throws Exception {
        String newNickname = "¯\\_(ツ)_/¯";
        mockMvc.perform(post("/settings/account")
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/account"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 취미 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get("/settings/hobby"))
                .andExpect(view().name("settings/hobby"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("hobby"));
    }

    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 취미 추가")
    @Transactional
    @Test
    void addHobby() throws Exception {
        HobbyForm hobbyForm = new HobbyForm();
        hobbyForm.setHobbyTitle("취미1");

        mockMvc.perform(post("/settings/hobby/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Hobby hobby = hobbyRepository.findByTitle("취미1").orElseThrow();
        assertNotNull(hobby);
        Account jongchan = accountRepository.findByNickname("jongchan");
        assertTrue(jongchan.getHobby().contains(hobby));
    }

    @WithUserDetails(value="jongchan",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 취미 삭제")
    @Transactional
    @Test
    void removeTag() throws Exception {
        Account jongchan = accountRepository.findByNickname("jongchan");
        Hobby hobby = hobbyRepository.save(Hobby.builder().title("취미1").build());
        accountService.addHobby(jongchan, hobby);

        assertTrue(jongchan.getHobby().contains(hobby));

        HobbyForm hobbyForm = new HobbyForm();
        hobbyForm.setHobbyTitle("취미1");

        mockMvc.perform(post("/settings/hobby/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hobbyForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(jongchan.getHobby().contains(hobby));
    }
}